import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 12345);
        authorization(s);
        s.close();
    }


    public static void authorization(Socket s) throws IOException {
        Scanner data = new Scanner(System.in);
        //зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        System.out.println(bf.readLine());
        int number = data.nextInt();
        pr.println(number); //список доступних операцій
        pr.flush();

        switch (number){
            case 1:
                registration(s); // реєстрація нового користувача
                break;
            case 2:
                exit(s); //вхід в систему
                break;
            case 3:
                break;
            default:
                System.out.println("Некоректні дані");
                authorization(s);
        }

    }

    public static void registration(Socket s) throws IOException{
        Scanner data = new Scanner(System.in);
        //зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        String result;
        /*користувач придумує логін, якщо в системі вже зареєстрований користувач
        * з даним логін то пропонується придумати інший*/
        do{

            System.out.println(bf.readLine());
            String login = data.next();
            pr.println(login);
            pr.flush();
            result = bf.readLine();
            if(result.equals("error")){
                System.out.println(bf.readLine());
            }
            else{
                System.out.println(result);
            }
        }while (result.equals("error"));


        /*придумуємо пароль та вводимо його повторно для підтвердження.
        * Якщо паролі не співпадають заповнюємо заново*/
        String cont;
        do{
            System.out.println(bf.readLine());
            String password1 = data.next();
            pr.println(password1);
            pr.flush();
            System.out.println(bf.readLine());
            String password2 = data.next();
            pr.println(password2);
            pr.flush();
            cont = bf.readLine();
        }while(cont.equals("continue"));

        /*Обераємо метод шифрування даних*/
        do{
            System.out.println("Оберіть метод шифрування: 1 - змінна символів, 2 - \"хитрий\" алгоритм");
            int number = data.nextInt();
            if(number == 1 || number == 2){
                pr.println(number);
                pr.flush();
                break;
            }
            else{
                System.out.println("некоректні дані.");
            }
        }while (true);

        authorization(s);
    }

    public static void exit (Socket s) throws IOException{
        Scanner data = new Scanner(System.in);
        //зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        /*Вхід в систему. Перевіряємо чи існує користувач з таким логіном
        * та коректність вводу паролю*/
        String n = "";
        do{
            System.out.println(bf.readLine());
            String login = data.next();
            pr.println(login);
            pr.flush();
            System.out.println( bf.readLine());
            String password = data.next();
            pr.println(password);
            pr.flush();

            String result = bf.readLine();
            System.out.println(result);
            if(result.equals("Вхід пройшов успішно")){
                break;
            }
            else if(result.equals("Невірний логін або пароль. Повторіть спробу")){
                System.out.println("1 - повторити спробу, 2 - реєстрація");
                n = data.next();
                pr.println(n);
                pr.flush();
                if (n.equals("2")){
                    break;
                }
            }

        }while (true);

        if(n.equals("2")){
            registration(s);
        }
        else{
            user_actions(s);
        }
    }

    public static void user_actions(Socket s) throws IOException {
        Scanner data = new Scanner(System.in);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        /*Список доступних дій по авторизованому доступі*/
        System.out.println(bf.readLine());
        int number = data.nextInt();
        pw.println(number);
        pw.flush();

        switch (number){
            case 1:
                record_file(s);
                break;
            case 2:
                download_file(s);
                break;
            case 3:
                show_list(s);
                break;
            case 4:
                authorization(s);
                break;
            default:

        }

    }

    public static void show_list(Socket s) throws IOException{
        /*Список файлів, які користувач передав на сервер
        (Якщо файли відсутні виводиться пустий рядок)*/
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        System.out.println(bf.readLine());
        System.out.println("ok");
        user_actions(s);
    }

    public static void record_file(Socket s) throws IOException {
        /*вводимо текст. Зберігаємо?
        * так: вводимо імя файлу. Перевіряємо чи не зайеяте імя. Зберігаємо на сервері*/
        Scanner data = new Scanner(System.in);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pw = new PrintWriter(s.getOutputStream());

        System.out.println(bf.readLine());
        String text = data.nextLine();
        System.out.println("Зберегти текст?(1 - так, 2 - ні)");
        int number = data.nextInt();
        pw.println(number);
        pw.flush();

        String result;

        if(number == 1){
            text = encrypt(text, Integer.parseInt(bf.readLine()));
            pw.println(text);
            pw.flush();
            do{
                System.out.println("Введіть імя файлу: ");
                String name_file = data.next();
                pw.println(name_file);
                pw.flush();
                result = bf.readLine();
                System.out.println(result);
                if(result.equals("OK")){
                    System.out.println("Імя файлу зайнято. Оберіть інше");
                }
            }while (result.equals("OK"));
        }
        user_actions(s);
    }

    public static void download_file(Socket s) throws IOException{
        /*Вводимо назву файлу дані якого хочемо отримати.
         Пропонуємо користувачу розшифрувати дані.
         Виводимо результат*/
        Scanner data = new Scanner(System.in);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pw = new PrintWriter(s.getOutputStream());

        String n = "";

        String name_file;
        String result;
        do {
            System.out.println("Введіть назву файлу, дані якого бажаєте отримати: ");
            name_file = data.next();

            pw.println(name_file);
            pw.flush();

            result = bf.readLine();
            if(result.equals("NO")){
                System.out.println("Файл з даним імям відсутній");
                System.out.println("1 - повторити спробу, 2 - перейти до інших операція");
                n = data.next();
                pw.println(n);
                pw.flush();
                if(n.equals("2")){
                    break;
                }

            }

        }while (result.equals("NO"));

        if(n.equals("2")){
            user_actions(s);
        }
        else{
            String text = bf.readLine();
            System.out.println(text);
            FileWriter fw = new FileWriter(name_file + ".txt");
            fw.write(text);
            fw.close();

            System.out.println("Розшифрувати ровідомлення?(1 - так, 2 - ні)");
            int number = data.nextInt();
            pw.println(number);
            pw.flush();
            if(number == 1){
                int key = Integer.parseInt(bf.readLine());
                text = decryption(text, key);
                FileWriter fw1 = new FileWriter(name_file + ".txt");
                fw1.write(text);
                fw1.close();
            }

            user_actions(s);
        }


    }

    public static String encrypt(String text, int number){
        /*Взалежності від вибраного способу шифрування при реєстрації,
        * шифруємо текст введений користувачем*/
        String new_text = "";
        if(number == 1){
            for(int i =0; i < text.length(); i++){
                new_text += (char)((int)text.charAt(i)+2);
            }
        }
        if(number == 2){
            for(int i = 0; i < text.length(); i++){
                if(i % 2 == 0){
                    new_text += (char)((int)text.charAt(i)+1);
                }
                else{
                    new_text += (char)((int)text.charAt(i)-1);
                }
            }
        }
        System.out.println(new_text);
        return new_text;
    }

    public static String decryption(String text, int key){
        /*по відповідному публічному ключі розшифровуємо зашифрований текст*/
        String new_text = "";
        if(key == 1){
            for(int i = 0; i < text.length(); i++){
                new_text += (char)((int)text.charAt(i)-2);
            }
        }
        if(key == 2){
            for(int i = 0; i < text.length(); i++){
                if(i % 2 == 0){
                    new_text += (char)((int)text.charAt(i)-1);
                }
                else{
                    new_text += (char)((int)text.charAt(i)+1);
                }
            }
        }
        System.out.println(new_text);
        return new_text;
    }
}
