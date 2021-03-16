package eu.endermite.censura.config;

import java.util.Map;

public class CharReplacementMap {
    final char[] a;
    final char[] b;

    CharReplacementMap(Map<?,?> map) {
        this.a = new char[map.size()];
        this.b = new char[map.size()];

        int i = 0;
        for (Map.Entry<?,?> entry : map.entrySet()) {
            this.a[i] = tryParse(entry.getKey());
            this.b[i] = tryParse(entry.getValue());
            i++;
        }
    }

    private char tryParse(Object object) {
        if (object instanceof Character) {
            return (char)object;
        }
        if (object instanceof String) {
            if (((String)object).length() == 1) {
                return ((String)object).toCharArray()[0];
            } else {
                throw new IllegalArgumentException(object + " isn't one character");
            }
        }
        return tryParse(object.toString());
    }

    public String process(String in) {
        char[] value = in.toCharArray();

        for (int i = 0; i < value.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (a[j] == value[i]) {
                    value[i] = b[j];
                }
            }
        }

        return new String(value);
    }
}
