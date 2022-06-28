package src;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner entrada = new Scanner(System.in);
        int opcion;
        boolean salida = false;

        int[] coeficientes = new int[3];

        System.out.println("Introduzca los coeficientes A, B y  C de la funcion AX^2 + BX + C:");
        System.out.println("A: ");
        coeficientes[0] = inputOnlyInteger(entrada);
        System.out.println("B: ");
        coeficientes[1] = inputOnlyInteger(entrada);
        System.out.println("C: ");
        coeficientes[2] = inputOnlyInteger(entrada);

        while( salida == false){
            mostrarMenu();
            opcion = inputOnlyInteger(entrada);
            switch( opcion ){
                case 1:

                    waitPressKey();
                    break;
                case 2:

                    waitPressKey();
                    break;
                case 3:

                    waitPressKey();
                    break;
                case 4:

                    waitPressKey();
                    break;
                case 5:

                    waitPressKey();
                    break;
                case 6:
                    salida =true;
                    break;
                default:
                    System.out.println("Lo sentimos, opción incorrecta");
                    waitPressKey();
            }
        }

        System.out.println("Adios, gracias por utilizarnos");

    }

    /**
     * @brief Método encargado de mostrar el menu de la aplicación
     *
     * @author Luis Andrés Valido Fajardo
     */
    private static void mostrarMenu(){

        System.out.println("***************************************************");
        System.out.println("* Bienvenidos a la aplicación                     *");
        System.out.println("*                                                 *");
        System.out.println("* 1) para imprimir funcion.                       *");
        System.out.println("* 2) para evaluar funcion en x.                   *");
        System.out.println("* 3) para eliminar un animal.                     *");
        System.out.println("* 4) para hallar raices                           *");
        System.out.println("* 5) para cambiar valores de la funcion.          *");
        System.out.println("* 6) para salir de la aplicación.                 *");
        System.out.println("*                                                 *");
        System.out.println("* Presiones el número asociado a la operación     *");
        System.out.println("* que desea realizar.                             *");
        System.out.println("*                                                 *");
        System.out.println("***************************************************");
    }

    /**
     * @brief Método encargado de esperar que el usuario espere la tecla enter para continuar
     *
     * @author Luis Andrés Valido Fajardo
     */
    private static void waitPressKey() throws IOException {
        System.out.println("Presione una tecla para continuar.");
        char c = (char) System.in.read();
    }

    /**
     * @brief Función encargado de leer exactamente un entero por la consola.
     * Mientras el usuario no entre un valor con esas características el método
     * se queda esperando por dicho valor.
     *
     * @param _input Varaible de tipo Scanner que esta conectado a la consola a
     * través de la cual se recibe la información que introduce el usuario.
     *
     * @return El valor entero introducido por el usuario por consola.
     *
     * @author Luis Andrés Valido Fajardo
     */
    private static int inputOnlyInteger(Scanner _input) {
        String data = _input.next();
        while (isInteger(data) == false) {
            System.out.println("El valor introducido no es un valor entero. \n"
                    + "Por favor vuelva a intentarlo.");
            data = _input.next();
        }
        return Integer.parseInt(data);
    }

    /**
     * @brief Función encargado de comprobar si una cadena de caracteres puede
     * ser convertida en un numero entero sin problema.
     *
     * @param strNum Variable de tipo cadena que almacena una secuencia de
     * caracteres la cual se quiere comprobar que todos son digitos.
     *
     * @return Verdadero si la candena puede ser convertida a un entero o Falso
     * en caso contrario.
     *
     * @author Luis Andrés Valido Fajardo
     */
    private static boolean isInteger(String strNum) {
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    /**
     * @brief Función encargado de comprobar si una cadena de caracteres puede
     * ser convertida en un numero real sin problema.
     *
     * @param strNum Variable de tipo cadena que almacena una secuencia de
     * caracteres la cual se quiere comprobar que todos son digitos.
     *
     * @return Verdadero si la candena puede ser convertida a un real o Falso
     * en caso contrario.
     *
     * @author Luis Andrés Valido Fajardo
     */
    private static boolean isDouble(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

}

