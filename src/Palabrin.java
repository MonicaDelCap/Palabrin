import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;


public class Palabrin {
    public static Random rand = new Random();
    public static Scanner writer = new Scanner(System.in);
    public static String[] dictionary;
    public static String[] globalDictionary;

    static {
        try {
            globalDictionary = getDictionary("palabrasDificiles.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        palabrin();
        //System.out.println(getFileLen("palabrasFaciles.txt"));
    }

    public static void palabrin() throws FileNotFoundException {

        do {
            setDifficulty();

            int intentos = 0;
            int puntuacion = 1000;

            //asigno a randword una palabra aleatoria del diccionario
            String randWord = getRandomWord(dictionary);

            String word = "";

            String output = Color.WHITE_BRIGHT+getStringOf(randWord.length(), '_');
            //output de momento es "________". Se actualizara cuando introduzcamos un intento

            //se utilizara para volcarle una letra pista
            char pista;
            System.out.println(output);

            do {
                //word contiene el input del usuario
                word = getWord().toLowerCase();

                //si introducimos "poke", el juego nos da una pista
                if (word.equalsIgnoreCase("poke")) {
                    do {
                        pista = randWord.charAt(rand.nextInt(randWord.length()));
                    } while (contains(output, pista));
                    System.out.println(Color.WHITE_BRIGHT+"Pista: "+Color.GREEN_BRIGHT+pista);
                    //para que vuelva al principio del bucle y no crea que "poke" es nuestro intento
                    continue;
                }

                //nos devuelve nuestro output siguiedo la logica del juego
                output = engine(word, randWord);

                //lo muestra por pantalla y suma un intento. Maximo hay 10.
                System.out.println(output);
                intentos++;

                if (intentos>=10) {
                    System.out.println("Lo siento, no has adivinado la palabra, la palaba era: "+randWord);
                    break;
                }
                //si output no contiene ningun "_", significa que el usuario acerto la palabra
                //y se romperá el bucle principal del juego
            } while (contains(output, '_'));

            //sistema de puntaje
            puntuacion = puntuacion-(intentos*100);

            if (intentos<10) {
                System.out.println("Felicidades, has acertado la palabra");
            }
            if (puntuacion>=0) {
                System.out.println("Tu puntuacion: "+puntuacion);
            }

        } while (seguirJugando());
    }


    //Utilidad: devuelve una palabra aleatoria
    public static String getRandomWord(String[] words) {
        Random rand = new Random();
        return words[rand.nextInt(words.length)];
    }


    //Utilidad: devuelve la palabra introducida por teclado
    public static String getWord() {
        String word;
        do {
            word = writer.nextLine();
            if (word.equalsIgnoreCase("poke")) {
                break;
            }
        } while (!contains(globalDictionary, word));
        return word;
    }


    //Utilidad: (logica del juego) devuelve el string en funcion de la respuesta del usuario
    public static String engine(String input, String randWord) {
        String[] inputChars = getArrayOf(getStringOf(input.length(), '_'));
        for (int i = 0; i<input.length(); i++) {
            //Si coinciden, se pinta de verde
            if (charInSamePositions(input, randWord, i)) {
                inputChars[i] = Color.GREEN_BOLD_BRIGHT+input.charAt(i);
            //sino, si contiene esa letra..
            } else if (contains(input, input.charAt(i)) && contains(randWord, input.charAt(i))) {
                //si hay mas ocurrencias de la letra en input que en randWord..
                if (ocurrencesOfChar(input, input.charAt(i)) > ocurrencesOfChar(randWord, input.charAt(i))) {
                    //se quita esa letra de input y colocamos "_"
                    inputChars[i] = Color.WHITE_BOLD_BRIGHT+"_";
                    input = replaceBy(input, i, ' ');
                } else {
                    //si hay las mismas ocurrencias o menos que en randWord, se pintarán de amarillo
                    inputChars[i] = Color.YELLOW_BOLD_BRIGHT+input.charAt(i);
                }

            } else {
                //si la letra no coincide con ninguna otra..
                inputChars[i] = Color.WHITE_BOLD_BRIGHT+"_";
            }
        }
        return stringArrayToString(inputChars);
    }


    //Utilidad: remplazar una caracter por otro
    public static String replaceBy(String s, int pos, char c) {
        char[] chars = toCharArray(s);
        chars[pos] = c;
        return charArrayToString(chars);
    }

    //Utilidad: devuelve las ocurrencias de un char en un string
    public static int ocurrencesOfChar(String a, char c) {
        int sum = 0;
        for (int i = 0; i<a.length(); i++)  {
            if (a.charAt(i) == c) {
                sum++;
            }
        }
        return sum;
    }

    //Utilidad: sirve para saber si dos palabras coinciden en un caracter en una posicion
    // (en la funcion palabrin() sirve para pintar el char de verde)
    public static boolean charInSamePositions(String a, String b, int pos) {
        return a.charAt(pos) == b.charAt(pos);
    }


    //Utilidad: devuelve la cantidad de lineas que tiene el fichero
    public static int getFileLen(String file) throws FileNotFoundException {
        int len = 0;
        Scanner sc = new Scanner(new FileReader(file));
        while (sc.hasNext()) {
            len++;
            String s = sc.nextLine();
        }
        return len;
    }

    //Utilidad: volcamos en el array "dictionary" todas las palabras del fichero que se introduzca como parametro
    public static String[] getDictionary(String file) throws FileNotFoundException {
        // array de Strings para cargar palabras de 8 caracteres
        String[] diccionario = new String[getFileLen(file)];

        // Utilizaremos un Scanner para cargar el fichero txt
        Scanner sc;
        int i = 0;

        try {
            // Cargamos el fichero txt guardado en la carpeta del proyecto
            sc = new Scanner(new FileReader(file));
            String str;

            //repetir hasta terminar de leer el fichero
            while (sc.hasNext()) {
                str = sc.next();

                // añadir palabra al diccionario
                diccionario[i] = str;
                i++;
            }


        } catch (FileNotFoundException e) {
            // asegurarse que la ruta y el nombre del fichero son correctos
            System.err.println("Fichero no encontrado");
        }
        return diccionario;
    }

    //Utilidad: configura el array "dictionary" en funcion de la dificultad
    public static String[] setDifficulty() throws FileNotFoundException {
        System.out.print(Color.CYAN_BOLD+"Elige la dificultad\n1-Facil      2-Normal    3-Dificil");
        int d = writer.nextInt();

        switch (d) {
            case 1: dictionary = getDictionary("palabrasFaciles.txt");
                break;
            case 2: dictionary = getDictionary("palabrasNormales.txt");
                break;
            case 3: dictionary = getDictionary("palabrasDificiles.txt");
                break;
        }
        return dictionary;
    }

    public static String stringArrayToString(String[] array) {
        StringBuilder b = new StringBuilder();
        for (String a: array) {
            b.append(a);
        }
        return b.toString();
    }

    public static String charArrayToString(char[] array) {
        StringBuilder b = new StringBuilder();
        for (char a: array) {
            b.append(a);
        }
        return b+"";
    }

    public static char[] toCharArray(String word) {
        char[] chars = new char[word.length()];
        for (int i = 0; i<word.length(); i++) {
            chars[i] = word.charAt(i);
        }
        
        return chars;
    }

    public static String getStringOf(int len, char c) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i<len; i++) {
            s.append(c);
        }
        return s+"";
    }

    public static String[] getArrayOf(String s) {
        String[] a = new String[s.length()];
        for (int i = 0; i<s.length(); i++) {
            a[i] = s.charAt(i)+"";
        }
        return a;
    }

    public static boolean contains(String s, char c) {
        for (int i = 0; i<s.length(); i++) {
            if (s.charAt(i)==c) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String[] s, String c) {
        for (String a: s) {
            if (a.equalsIgnoreCase(c)) {
                return true;
            }
        }
        return false;
    }


    static final String RESPUESTAS_POSITIVAS = "YSys";
    static final String RESPUESTAS_NEGATIVAS = "Nn";


    //Esta funcion simplemente devuelve verdadero o falso en funcion del input (si recibe una s/y devuelve verdadero, si no falso)
    public static boolean seguirJugando() {

        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            System.out.println(Color.WHITE_BOLD_BRIGHT+"DESEA JUGAR OTRA PARTIDA? (y/n)");
            input = scanner.nextLine();
        } while (RESPUESTAS_POSITIVAS.indexOf(input.charAt(0)) == -1 && RESPUESTAS_NEGATIVAS.indexOf(input.charAt(0)) == -1);

        if (RESPUESTAS_POSITIVAS.indexOf(input.charAt(0)) != -1) {
            return true;
        }
        return false;
    }


}

