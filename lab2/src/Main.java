import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int TIMEOUT = 1000;
    public static void main(String[] args){
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int startInd = 1;
        int endInd = 3500;
        for (int port = startInd; port<= endInd; port++){
            final int currPort = port;
            executor.submit(()->scanPort(currPort));
        }
        try {
            executor.shutdown();
            boolean result = executor.awaitTermination(1, TimeUnit.MINUTES);
            if (!result){
                System.err.println("[ERROR] Не получилось получить результат всех потоков");
            }
        }catch (InterruptedException e){
            System.err.println("[ERROR] main thread был прерван");
        }

    }
    private static void scanPort(int portNumber){
        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress("localhost", portNumber), TIMEOUT);
            System.out.println("Порт " + portNumber + " открыт. Имя=" +
                    ServiceResolver.getServiceName(portNumber));
        }catch (java.net.SocketTimeoutException e) {
            //System.out.println("Порт " + portNumber + " не отвечает (TIMEOUT)");
        } catch (Exception ignored) {//TODO change exception
            //System.out.println("Порт " + portNumber + " закрыт");
        }
    }
}
