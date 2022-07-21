import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        Socket s = ss.accept();
        System.out.println("Client connected");

        authorization(s);

    }

    public static void authorization(Socket s) throws IOException{
        //зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pw = new PrintWriter(s.getOutputStream());

        //авторизація
        pw.println("1 - реєстрація, 2 -  вхід, 3 - вихід із системи");
        pw.flush();

        int number = Integer.parseInt(bf.readLine());
        switch (number){
            case 1:
            {
                registration(s);
                break;
            }
            case 2:
            {
                exit(s);
                break;
            }
            case 3:
                break;
            default:{
                authorization(s);
            }
        }
    }

    public static void registration(Socket s) throws IOException{//зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pw = new PrintWriter(s.getOutputStream());

        String login;
        boolean it_is;
        //перевірка логіну
        do{
            FileReader fr = new FileReader("USERS.txt");
            Scanner scan = new Scanner(fr);
            it_is = false;
            pw.println("логін: ");
            pw.flush();
            login = bf.readLine();
            while (scan.hasNextLine()){
                String d = scan.nextLine();
                String[] dd = d.split(" ");
                if(dd[0].equals(login)){
                    it_is = true;
                }
            }
            if(it_is){
                pw.println("error");
                pw.flush();
                pw.println("Логін зайнято. Введіть новий");
                pw.flush();
            }
            else{
                pw.println("OK");
                pw.flush();
            }
        }while (it_is);


        //перевірка паролю
        String password1;
        String password2;
        do{
            pw.println("пароль: ");
            pw.flush();
            password1 = bf.readLine();
            pw.println("Повторний пароль: ");
            pw.flush();
            password2 = bf.readLine();
            if(password1.equals(password2)){
                pw.println("break");
                pw.flush();
                break;
            }
            else{
                pw.println("continue");
                pw.flush();
            }
        }while (true);
        String number = bf.readLine();
        BufferedWriter bw = new BufferedWriter(new FileWriter("USERS.txt", true));
        bw.append(login + " " + password1 + " " + number);
        bw.newLine();
        bw.close();

        FileWriter us = new FileWriter(login + ".txt");
        us.close();
        authorization(s);
    }

    public static void exit(Socket s) throws  IOException{
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pw = new PrintWriter(s.getOutputStream());

        String login;
        String key = "";
        String n = "";

        do{
            pw.println("Логін: ");
            pw.flush();
            login = bf.readLine();
            pw.println("Пароль: ");
            pw.flush();
            String password = bf.readLine();

            boolean it_is = false;

            FileReader fr = new FileReader("USERS.txt");
            Scanner scan = new Scanner(fr);
            while (scan.hasNextLine()){
                String[] data = (scan.nextLine()).split(" ");
                if(data[0].equals(login) && data[1].equals(password)){
                    key = data[2];
                    it_is = true;
                    break;
                }
            }
            if(it_is){
                pw.println("Вхід пройшов успішно");
                pw.flush();
                break;
            }
            else{
                pw.println("Невірний логін або пароль. Повторіть спробу");
                pw.flush();
                n = bf.readLine();
                if(n.equals("2")){
                    break;
                }
            }
        }while(true);

        if(n.equals("2")){
            registration(s);
        }
        else{
            user_actions(s, login, key);
        }
    }

    public static void user_actions(Socket s, String login, String key) throws IOException {
        //зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        pr.println("1 - запис файлу, 2 - скачати файл, 3 - показати список файлів, 4 - вихід");
        pr.flush();

        int number = Integer.parseInt(bf.readLine());

        switch (number){
            case 1:
                record_file(s, login, key);
                break;
            case 2:
                download_file(s, login, key);
                break;
            case 3:
                show_list(s, login, key);
                break;
            case 4:
                authorization(s);
                break;
            default:

        }

    }

    public static void show_list(Socket s, String login, String key) throws  IOException{
        //передача даних
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        FileReader fr = new FileReader(login + ".txt");
        Scanner scan = new Scanner(fr);
        String list = "";
        while (scan.hasNextLine()){
            list = scan.nextLine();
        }
        fr.close();

        pr.println(list);
        pr.flush();

        user_actions(s, login, key);
    }

    public static void record_file(Socket s, String login, String key) throws IOException {
        //зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        pr.println("Ввід тексту: ");
        pr.flush();

        int number = Integer.parseInt(bf.readLine());
        if(number == 1){
            pr.println(key);
            pr.flush();

            FileReader fr = new FileReader(login + ".txt");
            Scanner scan = new Scanner(fr);

            String text = bf.readLine();
            String name_file;
            boolean it_is;

            do{
                it_is = false;
                name_file = bf.readLine();
                if(scan.hasNextLine()){
                    String[] list_file = (scan.nextLine()).split(", ");
                    for(int i = 0; i < list_file.length; i++){
                        if(name_file.equals(list_file[i])){
                            it_is = true;
                        }
                    }
                }
                if(it_is){
                    pr.println("OK");
                    pr.flush();
                }
                else{
                    pr.println("NO");
                    pr.flush();
                }
            }while (it_is);


            FileWriter fw = new FileWriter(login + ".txt", true);
            fw.append(name_file + ", ");
            fw.close();

            FileWriter fr1 = new FileWriter(login + " " + name_file + ".txt");
            fr1.write(text);
            fr1.close();
        }
        user_actions(s, login, key);
    }

    public static void download_file(Socket s, String login, String key) throws IOException{
        //зчитування даних
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        //передача даних
        PrintWriter pr = new PrintWriter(s.getOutputStream());

        String name_file;
        boolean it_is;
        String n = "";
        do{
            it_is = false;
            name_file = bf.readLine();
            FileReader fr = new FileReader(login + ".txt");
            Scanner scan = new Scanner(fr);
            String[] dd = new String[0];
            while (scan.hasNextLine()){
                dd = (scan.nextLine()).split(", ");
            }
            for(int i = 0; i < dd.length; i++){
                if(dd[i].equals(name_file)){
                    it_is = true;
                    break;
                }
            }
            if(it_is){
                pr.println("OK");
                pr.flush();
            }
            else{
                pr.println("NO");
                pr.flush();
                n = bf.readLine();
                if (n.equals("2")){
                    break;
                }
            }

        }while (!(it_is));

        if(n.equals("2")){
            user_actions(s, login, key);
        }
        else{
            FileReader fr = new FileReader(login + " " + name_file + ".txt");
            Scanner scan = new Scanner(fr);
            String text = scan.nextLine();
            fr.close();

            pr.println(text);
            pr.flush();

            int number = Integer.parseInt(bf.readLine());
            if(number == 1){
                pr.println(key);
                pr.flush();
            }
            user_actions(s, login, key);
        }

    }
}