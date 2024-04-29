package com.example.sportsreservationsystembackend.utils;

import java.util.List;
import java.util.Objects;

/**
 * This class represents notifications util
 *
 * @Author https://github.com/honzaskypala/osloveni/blob/master/javascript/osloveni.js
 */
public class NotificationsUtil {

    /**
     * This method returns vocative form of name
     * @param name to be converted to vocative form
     * @return vocative form of name
     */
    // taken from https://github.com/honzaskypala/osloveni/blob/master/javascript/osloveni.js
    public static String vocative(String name) {
        String ljmeno;
        List<String> replacepair;
        char c;
        ljmeno = " " + name.toLowerCase();
        char thirdLastChar = ljmeno.charAt(ljmeno.length() - 3);
        boolean thirdCharIsI = thirdLastChar == 'i';
        char secondLastChar = ljmeno.charAt(ljmeno.length() - 2);
        char forthLastChar = ljmeno.charAt(ljmeno.length() - 4);
        boolean thirdLastCharIsV = thirdLastChar == 'v';
        char fifthLastChar = ljmeno.charAt(ljmeno.length() - 5);
        switch (ljmeno.charAt(ljmeno.length() - 1)) {
            case 'a':
                replacepair = secondLastChar == 'i' ? List.of("a", "e") : List.of("a", "o");
                break;
            case 'n':
                switch (secondLastChar) {
                    case 'o':
                        if (thirdCharIsI) {
                            replacepair = fifthLastChar == 'y' ? List.of("", "e") : List.of("", "");
                        } else {
                            replacepair = List.of("", "e");
                        }
                        break;
                    case 'i':
                        switch (thirdLastChar) {
                            case 'r':
                                if (forthLastChar == 'a') {
                                    replacepair = fifthLastChar == 'm' ? List.of("", "e") : List.of("", "");
                                } else {
                                    replacepair = List.of("", "");
                                }
                                break;
                            case 'l':
                                replacepair = forthLastChar == 'r' ? List.of("", "e") : List.of("", "");
                                break;
                            default:
                                replacepair = List.of("", "e");
                        }
                        break;
                    case 'í':
                        replacepair = thirdLastChar == 'r' ? List.of("", "") : List.of("", "e");
                        break;
                    case 'e':
                        replacepair = switch (thirdLastChar) {
                            case 'm' -> forthLastChar == 'm' ? List.of("", "e") : List.of("", "");
                            case 'r' -> forthLastChar == 'o' ? List.of("", "e") : List.of("", "");
                            default -> List.of("", "e");
                        };
                        break;
                    case 'y':
                        replacepair = thirdLastChar == 'r' ? List.of("", "e") : List.of("", "");
                        break;
                    case 'á':
                        replacepair = thirdLastChar == 'p' ? List.of("án", "ane") : List.of("", "e");
                        break;
                    default:
                        replacepair = secondLastChar == 'u' ? List.of("", "o") : List.of("", "e");
                }
                break;
            case 'l':
                switch (secondLastChar) {
                    case 'e':
                        switch (thirdLastChar) {
                            case 'i':
                                if (forthLastChar == 'r') {
                                    replacepair = fifthLastChar == 'u' ? List.of("", "") : List.of("", "i");
                                } else {
                                    replacepair = List.of("", "i");
                                }
                                break;
                            case 'r':
                                replacepair = forthLastChar == 'a' ? List.of("el", "le") : List.of("", "i");
                                break;
                            case 'v':
                                replacepair = fifthLastChar == 'p' ? List.of("el", "le") : List.of("el", "li");
                                break;
                            case 'k':
                                replacepair = forthLastChar == 'a' ? List.of("", "") : List.of("", "i");
                                break;
                            default:
                                replacepair = thirdLastChar == 'h' ? List.of("", "") : List.of("", "i");
                        }
                        break;
                    case 'i':
                        replacepair = thirdLastChar == 'a' ? List.of("", "o") : List.of("", "e");
                        break;
                    case 'ě':
                    case 'á':
                    case 's':
                        replacepair = List.of("", "i");
                        break;
                    case 'ů':
                        replacepair = List.of("ůl", "ole");
                        break;
                    default:
                        replacepair = List.of("", "e");
                }
                break;
            case 'm':
                if (secondLastChar == 'a') {
                    if (thirdCharIsI) {
                        replacepair = forthLastChar == 'r' ? List.of("", "") : List.of("", "e");
                    } else {
                        replacepair = List.of("", "e");
                    }
                } else {
                    replacepair = secondLastChar == 'ů' ? List.of("ům", "ome") : List.of("", "e");
                }
                break;
            case 'c':
                switch (secondLastChar) {
                    case 'e':
                        if (thirdLastCharIsV) {
                            replacepair = forthLastChar == 'š' ? List.of("vec", "evče") : List.of("ec", "če");
                        } else {
                            replacepair = List.of("ec", "če");
                        }
                        break;
                    case 'i':
                        replacepair = forthLastChar == 'o' ? List.of("", "i") : List.of("", "u");
                        break;
                    default:
                        replacepair = secondLastChar == 'a' ? List.of("", "u") : List.of("", "i");
                }
                break;
            case 'e':
                switch (secondLastChar) {
                    case 'n':
                        if (thirdLastChar == 'n') {
                            replacepair = ljmeno.charAt(ljmeno.length() - 7) == 'b' ? List.of("", "") : List.of("e", "o");
                        } else {
                            replacepair = thirdLastChar == 'g' ? List.of("e", "i") : List.of("", "");
                        }
                        break;
                    case 'c':
                        if (thirdLastChar == 'i') {
                            replacepair = forthLastChar == 'r' ? List.of("e", "i") : List.of("", "");
                        } else {
                            replacepair = thirdLastCharIsV ? List.of("", "") : List.of("e", "i");
                        }
                        break;
                    case 'd':
                        replacepair = thirdLastChar == 'l' ? List.of("e", "o") : List.of("", "");
                        break;
                    case 'g':
                        if (thirdLastChar == 'r') {
                            replacepair = forthLastChar == 'a' ? List.of("", "") : List.of("e", "i");
                        } else {
                            replacepair = List.of("e", "i");
                        }
                        break;
                    case 'l':
                        if (thirdLastChar == 'l') {
                            replacepair = switch (forthLastChar) {
                                case 'e' -> List.of("e", "o");
                                case 'o' -> List.of("", "");
                                default -> List.of("e", "i");
                            };
                        } else {
                            replacepair = List.of("", "");
                        }
                        break;
                    case 's':
                        replacepair = thirdLastChar == 's' ? List.of("e", "i") : List.of("e", "o");
                        break;
                    case 'h':
                        replacepair = thirdLastChar == 't' ? List.of("", "") : List.of("e", "i");
                        break;
                    default:
                        replacepair = secondLastChar == 'k' ? List.of("", "u") : List.of("", "");
                }
                break;
            case 's':
                switch (secondLastChar) {
                    case 'e':
                        switch (thirdLastChar) {
                            case 'n':
                                replacepair = switch (forthLastChar) {
                                    case 'e' -> List.of("s", "");
                                    case 'á' -> List.of("", "i");
                                    default -> List.of("", "");
                                };
                                break;
                            case 'l':
                                if (forthLastChar == 'u') {
                                    replacepair = fifthLastChar == 'j' ? List.of("", "i") : List.of("s", "");
                                } else {
                                    c = forthLastChar;
                                    replacepair = c == 'o' || c == 'r' ? List.of("", "i") : List.of("s", "");
                                }
                                break;
                            case 'r':
                                replacepair = forthLastChar == 'e' ? List.of("s", "ro") : List.of("", "i");
                                break;
                            case 'd':
                            case 't':
                            case 'm':
                                replacepair = List.of("s", "");
                                break;
                            case 'u':
                                replacepair = List.of("s", "u");
                                break;
                            case 'p':
                                replacepair = List.of("es", "se");
                                break;
                            case 'x':
                                replacepair = List.of("es", "i");
                                break;
                            default:
                                replacepair = List.of("", "i");
                        }
                        break;
                    case 'i':
                        switch (thirdLastChar) {
                            case 'r':
                                if (forthLastChar == 'a') {
                                    replacepair = fifthLastChar == 'p' ? List.of("s", "de") : List.of("s", "to");
                                } else {
                                    replacepair = List.of("", "i");
                                }
                                break;
                            case 'n':
                                replacepair = forthLastChar == 'f' ? List.of("s", "de") : List.of("", "i");
                                break;
                            default:
                                replacepair = thirdLastChar == 'm' ? List.of("s", "do") : List.of("", "i");
                        }
                        break;
                    case 'o':
                        replacepair = switch (thirdLastChar) {
                            case 'm' -> forthLastChar == 'i' ? List.of("os", "e") : List.of("", "i");
                            case 'k' -> List.of("", "e");
                            case 'x' -> List.of("os", "i");
                            default -> List.of("os", "e");
                        };
                        break;
                    case 'a':
                        replacepair = switch (thirdLastChar) {
                            case 'r' -> forthLastChar == 'a' ? List.of("", "i") : List.of("as", "e");
                            case 'l' -> forthLastChar == 'l' ? List.of("s", "do") : List.of("", "i");
                            default -> thirdLastChar == 'y' ? List.of("as", "e") : List.of("", "i");
                        };
                        break;
                    case 'r':
                        replacepair = thirdLastChar == 'a' ? List.of("s", "te") : List.of("", "i");
                        break;
                    case 'u':
                        switch (thirdLastChar) {
                            case 'n':
                                if (forthLastChar == 'e') {
                                    replacepair = fifthLastChar == 'v' ? List.of("us", "ero") : List.of("", "i");
                                } else {
                                    replacepair = forthLastChar == 'g' ? List.of("", "i") : List.of("us", "e");
                                }
                                break;
                            case 'e':
                                replacepair = forthLastChar == 'z' ? List.of("zeus", "die") : List.of("us", "e");
                                break;
                            case 'm':
                                replacepair = forthLastChar == 't' ? List.of("us", "e") : List.of("", "i");
                                break;
                            case 'g':
                            case 'a':
                                replacepair = List.of("", "i");
                                break;
                            case 'h':
                                replacepair = List.of("", "e");
                                break;
                            case 'c':
                            case 'k':
                                replacepair = List.of("s", "");
                                break;
                            default:
                                replacepair = List.of("us", "e");
                        }
                        break;
                    case 'y':
                        replacepair = forthLastChar == 'a' ? List.of("", "i") : List.of("", "");
                        break;
                    default:
                        replacepair = secondLastChar == 'é' ? List.of("s", "e") : List.of("", "i");
                }
                break;
            case 'o':
                replacepair = secondLastChar == 'l' ? List.of("", "i") : List.of("", "");
                break;
            case 'x':
                replacepair = secondLastChar == 'n' ? List.of("x", "go") : List.of("", "i");
                break;
            case 'i':
                switch (secondLastChar) {
                    case 'n' -> replacepair = forthLastChar == 'e' ? List.of("", "") : List.of("", "o");
                    case 'm' -> replacepair = thirdLastChar == 'a' ? List.of("", "") : List.of("", "o");
                    case 'r' -> replacepair = thirdCharIsI ? List.of("", "o") : List.of("", "");
                    default -> {
                        c = secondLastChar;
                        replacepair = c == 's' || c == 'a' || c == 'o' || c == 'c' || c == 't' ? List.of("", "i") : List.of("", "");
                    }
                }
                break;
            case 't':
                replacepair = switch (secondLastChar) {
                    case 'i' -> thirdLastChar == 'l' ? List.of("", "e") : List.of("", "");
                    case 'u' -> thirdLastChar == 'r' ? List.of("", "") : List.of("", "e");
                    default -> List.of("", "e");
                };
                break;
            case 'r':
                switch (secondLastChar) {
                    case 'e':
                        switch (thirdLastChar) {
                            case 'd':
                                if (forthLastChar == 'i') {
                                    replacepair = fifthLastChar == 'e' ? List.of("", "e") : List.of("", "i");
                                } else {
                                    replacepair = List.of("er", "re");
                                }
                                break;
                            case 't':
                                replacepair = switch (forthLastChar) {
                                    case 'e' -> fifthLastChar == 'p' ? List.of("", "e") : List.of("", "o");
                                    case 's' -> fifthLastChar == 'o' ? List.of("", "e") : List.of("", "");
                                    default -> forthLastChar == 'n' ? List.of("", "i") : List.of("", "e");
                                };
                                break;
                            default:
                                c = thirdLastChar;
                                replacepair = c == 'g' || c == 'k' ? List.of("er", "ře") : List.of("", "e");
                        }
                        break;
                    case 'a':
                        replacepair = switch (thirdLastChar) {
                            case 'm' -> forthLastChar == 'g' ? List.of("", "") : List.of("", "e");
                            case 'l' -> fifthLastChar == 'p' ? List.of("", "") : List.of("", "e");
                            default -> List.of("", "e");
                        };
                        break;
                    case 'o':
                        replacepair = thirdLastChar == 'n' ? List.of("", "o") : List.of("", "e");
                        break;
                    default:
                        c = secondLastChar;
                        replacepair = c == 'd' || c == 't' || c == 'b' ? List.of("r", "ře") : List.of("", "e");
                }
                break;
            case 'j':
                replacepair = switch (secondLastChar) {
                    case 'o' -> thirdLastChar == 't' ? List.of("oj", "ý") : List.of("", "i");
                    case 'i' -> thirdLastChar == 'd' ? List.of("", "i") : List.of("ij", "ý");
                    default -> secondLastChar == 'y' ? List.of("yj", "ý") : List.of("", "i");
                };
                break;
            case 'd':
                replacepair = switch (secondLastChar) {
                    case 'i' -> thirdLastChar == 'r' ? List.of("", "") : List.of("", "e");
                    case 'u' -> thirdLastChar == 'a' ? List.of("", "") : List.of("", "e");
                    default -> List.of("", "e");
                };
                break;
            case 'y':
                c = secondLastChar;
                replacepair = c == 'a' || c == 'g' || c == 'o' ? List.of("", "i") : List.of("", "");
                break;
            case 'h':
                replacepair = switch (secondLastChar) {
                    case 'c' -> switch (thirdLastChar) {
                        case 'r' -> List.of("", "i");
                        case 'ý' -> List.of("", "");
                        default -> List.of("", "u");
                    };
                    case 't' -> thirdLastChar == 'e' ? List.of("", "e") : List.of("", "i");
                    case 'a' -> thirdLastChar == 'o' ? List.of("", "u") : List.of("", "");
                    default -> secondLastChar == 'ů' ? List.of("ůh", "ože") : List.of("", "i");
                };
                break;
            case 'v':
                replacepair = secondLastChar == 'ů' ? List.of("", "") : List.of("", "e");
                break;
            case 'u':
                replacepair = secondLastChar == 't' ? List.of("", "") : List.of("", "i");
                break;
            case 'k':
                if (secondLastChar == 'ě') {
                    char thirdLastCharWithHook = switch (thirdLastChar) {
                        case 'd' -> 'ď';
                        case 't' -> 'ť';
                        case 'n' -> 'ň';
                        default -> thirdLastChar;
                    };
                    replacepair = List.of('d','t','n').contains(thirdLastChar) ? List.of(thirdLastChar + "ěk", thirdLastCharWithHook + "ku") : List.of(thirdLastChar + "k", "ku");
                } else {
                    replacepair = secondLastChar == 'e' ? List.of("ek", "ku") : List.of("", "u");
                }
                break;
            case 'g':
                if (secondLastChar == 'i') {
                    replacepair = thirdLastChar == 'e' ? List.of("", "") : List.of("", "u");
                } else {
                    replacepair = List.of("", "u");
                }
                break;
            case 'ň':
                replacepair = secondLastChar == 'o' ? List.of("ň", "ni") : List.of("ůň", "oni");
                break;
            case 'f':
            case 'p':
            case 'b':
                replacepair = List.of("", "e");
                break;
            case 'w':
            case 'í':
            case 'á':
            case 'ý':
            case 'ů':
            case 'é':
                replacepair = List.of("", "");
                break;
            default:
                replacepair = List.of("", "i");
        }

        if (Objects.equals(replacepair.get(0), "") && Objects.equals(replacepair.get(1), "")) {
            return name;
        } else if (Objects.equals(replacepair.get(1), "")) {
            return name.substring(0, name.length() - replacepair.get(0).length());
        } else if (Objects.equals(replacepair.get(0), "")) {
            return name + (name.charAt(name.length() - 1) == name.charAt(name.length() - 1) ? replacepair.get(1) : replacepair.get(1).toUpperCase());
        } else {
            String replaceending = name.substring(name.length() - replacepair.get(0).length());
            if (replaceending.toUpperCase().equals(replaceending)) {
                return name.substring(0, name.length() - replacepair.get(0).length()) + replacepair.get(1).toUpperCase();
            } else if (replaceending.matches("^[A-ZÁČĎÉÍŇÓŘŠŤÚÝŽ][a-záčďéěíňóřšťúůýž]*$")) {
                String[] words = replacepair.get(1).split("\\s+");
                StringBuilder result = new StringBuilder();
                for (String word : words) {
                    result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
                }
                return name.substring(0, name.length() - replacepair.get(0).length()) + result.toString().trim();
            } else if (Character.isUpperCase(name.charAt(name.length() - 1))) {
                return name.substring(0, name.length() - replacepair.get(0).length()) + replacepair.get(1).toUpperCase();
            } else {
                return name.substring(0, name.length() - replacepair.get(0).length()) + replacepair.get(1);
            }
        }
    }
}
