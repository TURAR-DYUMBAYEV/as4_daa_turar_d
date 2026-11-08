import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/** JSON transport used by the supplied JSON Lines interface. */
final class Json {
    private final String source;
    private int index;

    private Json(String source) {
        this.source = source;
    }

    static Object parse(String source) {
        return new Json(source).value();
    }

    private void skipWhitespace() {
        while (index < source.length() && Character.isWhitespace(source.charAt(index))) {
            index++;
        }
    }

    private Object value() {
        skipWhitespace();
        char first = source.charAt(index);
        if (first == '"') return string();
        if (first == '{') return object();
        if (first == '[') return array();
        if (source.startsWith("null", index)) { index += 4; return null; }
        if (source.startsWith("true", index)) { index += 4; return true; }
        if (source.startsWith("false", index)) { index += 5; return false; }
        int start = index;
        if (first == '-') index++;
        while (index < source.length() && Character.isDigit(source.charAt(index))) index++;
        return Long.parseLong(source.substring(start, index));
    }

    private Map<String, Object> object() {
        index++;
        Map<String, Object> result = new LinkedHashMap<>();
        skipWhitespace();
        if (source.charAt(index) == '}') { index++; return result; }
        while (true) {
            String key = string();
            skipWhitespace();
            index++;
            result.put(key, value());
            skipWhitespace();
            if (source.charAt(index++) == '}') return result;
        }
    }

    private List<Object> array() {
        index++;
        List<Object> result = new ArrayList<>();
        skipWhitespace();
        if (source.charAt(index) == ']') { index++; return result; }
        while (true) {
            result.add(value());
            skipWhitespace();
            if (source.charAt(index++) == ']') return result;
        }
    }

    private String string() {
        skipWhitespace();
        index++;
        StringBuilder result = new StringBuilder();
        while (true) {
            char current = source.charAt(index++);
            if (current == '"') return result.toString();
            if (current == '\\') {
                current = source.charAt(index++);
                switch (current) {
                    case 'n' -> current = '\n';
                    case 'r' -> current = '\r';
                    case 't' -> current = '\t';
                    case 'b' -> current = '\b';
                    case 'f' -> current = '\f';
                    case 'u' -> { current = (char) Integer.parseInt(source.substring(index, index + 4), 16); index += 4; }
                    default -> { }
                }
            }
            result.append(current);
        }
    }

    static String write(Object value) {
        if (value == null) return "null";
        if (value instanceof String text) {
            StringBuilder result = new StringBuilder("\"");
            for (char current : text.toCharArray()) {
                if (current == '"' || current == '\\') result.append('\\').append(current);
                else if (current < 32) result.append(String.format("\\u%04x", (int) current));
                else result.append(current);
            }
            return result.append('"').toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringJoiner result = new StringJoiner(",", "{", "}");
            for (Map.Entry<?, ?> entry : map.entrySet()) result.add(write(entry.getKey().toString()) + ":" + write(entry.getValue()));
            return result.toString();
        }
        if (value instanceof Iterable<?> iterable) {
            StringJoiner result = new StringJoiner(",", "[", "]");
            for (Object item : iterable) result.add(write(item));
            return result.toString();
        }
        if (value.getClass().isArray()) {
            StringJoiner result = new StringJoiner(",", "[", "]");
            for (int i = 0; i < Array.getLength(value); i++) result.add(write(Array.get(value, i)));
            return result.toString();
        }
        return value.toString();
    }
}
