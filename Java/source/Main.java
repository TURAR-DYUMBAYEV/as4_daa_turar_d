import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class Main {
    @SuppressWarnings("unchecked")
    static Object handle(Map<String, Object> input) {
        return switch ((String) input.get("task")) {
            case "ping" -> Map.of("status", "ready");
            case "lcs" -> Algorithms.lcs(input);
            case "merge" -> Algorithms.merge(input);
            case "tree" -> Algorithms.tree(input);
            case "naive" -> Algorithms.naive(input);
            case "kmp" -> Algorithms.kmp(input);
            case "rk" -> Algorithms.rk(input);
            case "suffix" -> Algorithms.suffix(input);
            case "aho" -> Algorithms.aho(input);
            default -> throw new IllegalArgumentException("Unknown task");
        };
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                Map<String, Object> request = (Map<String, Object>) Json.parse(line);
                System.out.println(Json.write(handle(request)));
            } catch (UnsupportedOperationException error) {
                System.out.println(Json.write(Map.of("error", error.getMessage())));
            }
        }
    }
}
