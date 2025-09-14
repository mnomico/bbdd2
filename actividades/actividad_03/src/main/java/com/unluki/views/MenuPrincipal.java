package com.unluki.views;

import java.util.Scanner;

public class MenuPrincipal {
    private Scanner scanner;
    private ArticuloView articuloView;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.articuloView = new ArticuloView();
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. ABM y Consulta de Artículos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();
            switch (opcion) {
                case 1:
                    articuloView.mostrarMenu();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);

        scanner.close();
    }

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}