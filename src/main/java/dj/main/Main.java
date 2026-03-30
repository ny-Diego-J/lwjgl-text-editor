package dj.main;

public class Main {
    public static void main(String[] args) {
        Controller ct;
        if (args.length < 1) ct = new Controller("default.txt");
        else ct = new Controller(args[0]);
        ct.run();
    }
}