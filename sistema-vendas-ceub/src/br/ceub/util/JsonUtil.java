package br.ceub.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Conversor JSON minimalista escrito apenas com a biblioteca padrao do Java.
 *
 * Motivo de existir: a API REST e o JWT precisam ler/escrever JSON, mas o
 * ambiente deste projeto nao tem acesso a um gerenciador de dependencias
 * (Maven/Gradle) nem a internet para baixar bibliotecas como Gson ou
 * Jackson. Para manter o projeto "zero dependencias externas" e 100%
 * compilavel com apenas o JDK, implementamos aqui um conversor simples,
 * suficiente para os tipos usados no projeto: Map, List, String, Number,
 * Boolean e null.
 *
 * Em um projeto profissional com Maven/Gradle disponiveis, o recomendado
 * seria substituir esta classe por uma biblioteca consolidada (Gson,
 * Jackson, etc). Aqui ela cumpre o mesmo papel de forma didatica.
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    // ---------------------------------------------------------------
    // ESCRITA (objeto Java -> texto JSON)
    // ---------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static String toJson(Object valor) {
        StringBuilder sb = new StringBuilder();
        escrever(valor, sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void escrever(Object valor, StringBuilder sb) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof String) {
            escreverString((String) valor, sb);
        } else if (valor instanceof Number || valor instanceof Boolean) {
            sb.append(valor.toString());
        } else if (valor instanceof Map) {
            escreverMapa((Map<String, Object>) valor, sb);
        } else if (valor instanceof List) {
            escreverLista((List<Object>) valor, sb);
        } else if (valor instanceof Enum) {
            escreverString(((Enum<?>) valor).name(), sb);
        } else if (eTipoSimples(valor)) {
            // tipos como LocalDate/LocalDateTime: viram String no formato padrao do toString()
            escreverString(valor.toString(), sb);
        } else {
            // qualquer objeto de modelo (Cliente, Produto, Venda, ItemVenda...) e
            // convertido para um JSON de verdade lendo seus metodos getXxx()/isXxx(),
            // em vez de virar uma String com o resultado de toString().
            escreverMapa(converterObjetoParaMapa(valor), sb);
        }
    }

    private static boolean eTipoSimples(Object valor) {
        return valor.getClass().getPackageName().startsWith("java.time");
    }

    /**
     * Converte um objeto de modelo (POJO) em um {@code Map<String,Object>}
     * lendo, via reflection, todos os seus metodos publicos sem parametros
     * que comecem com "get" ou "is" (convencao de getters do JavaBeans).
     * Isso permite que classes como {@link br.ceub.model.Cliente},
     * {@link br.ceub.model.Produto} e {@link br.ceub.model.Venda} sejam
     * serializadas como JSON real (com seus campos), sem que a API REST
     * precise de codigo manual de conversao para cada classe.
     */
    private static Map<String, Object> converterObjetoParaMapa(Object objeto) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        for (java.lang.reflect.Method metodo : objeto.getClass().getMethods()) {
            String nomeMetodo = metodo.getName();
            boolean ehGetter = (nomeMetodo.startsWith("get") && nomeMetodo.length() > 3 && !nomeMetodo.equals("getClass"))
                    || (nomeMetodo.startsWith("is") && nomeMetodo.length() > 2);
            if (!ehGetter || metodo.getParameterCount() != 0) {
                continue;
            }
            try {
                Object valorCampo = metodo.invoke(objeto);
                String nomeCampo = extrairNomeCampo(nomeMetodo);
                mapa.put(nomeCampo, valorCampo);
            } catch (Exception e) {
                // getter que falhou ao ser chamado (raro): ignora esse campo
            }
        }
        return mapa;
    }

    private static String extrairNomeCampo(String nomeMetodo) {
        String semPrefixo = nomeMetodo.startsWith("get") ? nomeMetodo.substring(3) : nomeMetodo.substring(2);
        if (semPrefixo.isEmpty()) {
            return semPrefixo;
        }
        return Character.toLowerCase(semPrefixo.charAt(0)) + semPrefixo.substring(1);
    }

    private static void escreverMapa(Map<String, Object> mapa, StringBuilder sb) {
        sb.append('{');
        boolean primeiro = true;
        for (Map.Entry<String, Object> entrada : mapa.entrySet()) {
            if (!primeiro) {
                sb.append(',');
            }
            primeiro = false;
            escreverString(entrada.getKey(), sb);
            sb.append(':');
            escrever(entrada.getValue(), sb);
        }
        sb.append('}');
    }

    private static void escreverLista(List<Object> lista, StringBuilder sb) {
        sb.append('[');
        boolean primeiro = true;
        for (Object item : lista) {
            if (!primeiro) {
                sb.append(',');
            }
            primeiro = false;
            escrever(item, sb);
        }
        sb.append(']');
    }

    private static void escreverString(String texto, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    // ---------------------------------------------------------------
    // LEITURA (texto JSON -> objeto Java: Map, List, String, Double, Boolean, null)
    // ---------------------------------------------------------------

    public static Object parse(String json) {
        Parser parser = new Parser(json);
        Object valor = parser.parseValor();
        parser.pularEspacos();
        return valor;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObjeto(String json) {
        Object valor = parse(json);
        if (valor instanceof Map) {
            return (Map<String, Object>) valor;
        }
        throw new IllegalArgumentException("JSON informado nao representa um objeto: " + json);
    }

    /**
     * Parser recursivo descendente simples para JSON.
     */
    private static class Parser {
        private final String texto;
        private int pos;

        Parser(String texto) {
            this.texto = texto;
            this.pos = 0;
        }

        Object parseValor() {
            pularEspacos();
            if (pos >= texto.length()) {
                throw new IllegalArgumentException("JSON incompleto");
            }
            char c = texto.charAt(pos);
            if (c == '{') {
                return parseObjetoInterno();
            } else if (c == '[') {
                return parseArrayInterno();
            } else if (c == '"') {
                return parseStringInterno();
            } else if (c == 't' || c == 'f') {
                return parseBooleanInterno();
            } else if (c == 'n') {
                pos += 4; // "null"
                return null;
            } else {
                return parseNumeroInterno();
            }
        }

        private Map<String, Object> parseObjetoInterno() {
            Map<String, Object> mapa = new LinkedHashMap<>();
            pos++; // consome '{'
            pularEspacos();
            if (pos < texto.length() && texto.charAt(pos) == '}') {
                pos++;
                return mapa;
            }
            while (true) {
                pularEspacos();
                String chave = parseStringInterno();
                pularEspacos();
                pos++; // consome ':'
                Object valor = parseValor();
                mapa.put(chave, valor);
                pularEspacos();
                if (pos < texto.length() && texto.charAt(pos) == ',') {
                    pos++;
                } else {
                    break;
                }
            }
            pularEspacos();
            pos++; // consome '}'
            return mapa;
        }

        private List<Object> parseArrayInterno() {
            List<Object> lista = new ArrayList<>();
            pos++; // consome '['
            pularEspacos();
            if (pos < texto.length() && texto.charAt(pos) == ']') {
                pos++;
                return lista;
            }
            while (true) {
                Object valor = parseValor();
                lista.add(valor);
                pularEspacos();
                if (pos < texto.length() && texto.charAt(pos) == ',') {
                    pos++;
                } else {
                    break;
                }
            }
            pularEspacos();
            pos++; // consome ']'
            return lista;
        }

        private String parseStringInterno() {
            pos++; // consome aspas de abertura
            StringBuilder sb = new StringBuilder();
            while (texto.charAt(pos) != '"') {
                char c = texto.charAt(pos);
                if (c == '\\') {
                    pos++;
                    char proximo = texto.charAt(pos);
                    switch (proximo) {
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'u':
                            String hex = texto.substring(pos + 1, pos + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                            break;
                        default: sb.append(proximo);
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            pos++; // consome aspas de fechamento
            return sb.toString();
        }

        private Boolean parseBooleanInterno() {
            if (texto.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            } else {
                pos += 5;
                return Boolean.FALSE;
            }
        }

        private Double parseNumeroInterno() {
            int inicio = pos;
            while (pos < texto.length() && "-+.0123456789eE".indexOf(texto.charAt(pos)) >= 0) {
                pos++;
            }
            return Double.parseDouble(texto.substring(inicio, pos));
        }

        void pularEspacos() {
            while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) {
                pos++;
            }
        }
    }
}
