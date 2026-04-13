import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class ServiceResolver {
    private static final String SERVICE_PATH = "C:\\Windows\\System32\\drivers\\etc\\services";
    private static final Map<Integer, String> SERVICES = new HashMap<>();
    static{
        loadServices();
    }
    private static void loadServices(){
        try (BufferedReader reader = new BufferedReader(new FileReader(SERVICE_PATH))){
            String line;
            while ((line = reader.readLine()) != null){
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;

                String serviceName = parts[0];
                String[] portParts = parts[1].split("/");
                if (portParts.length != 2){
                    continue;
                }
                int port = Integer.parseInt(portParts[0]);
                String protocol = portParts[1];

                if (!protocol.equalsIgnoreCase("tcp")) {
                    continue;
                }
                SERVICES.put(port, serviceName);
            }

        } catch (NumberFormatException e) {
            System.err.println("[WARN] Не удалось распарсить строку " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[ERROR] Не удалось прочитать файл services: " + e.getMessage());
        }
    }
    public static String getServiceName(int port){
        return SERVICES.getOrDefault(port, "Unknown");
    }
}
