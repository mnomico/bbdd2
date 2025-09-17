package com.unluki.views;

import com.unluki.controllers.SucursalController;
import com.unluki.models.Sucursal;

import java.util.List;
import java.util.Scanner;

public class SucursalView {
    private final Scanner scanner;
    private final SucursalController sucursalController;

    public SucursalView() {
        this.scanner = new Scanner(System.in);
        this.sucursalController = new SucursalController();
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- ABM y Consulta de Sucursales ---");
            System.out.println("1. Agregar sucursal");
            System.out.println("2. Eliminar sucursal");
            System.out.println("3. Modificar sucursal");
            System.out.println("4. Buscar sucursal");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    agregarSucursal();
                    break;
                case 2:
                    eliminarSucursal();
                    break;
                case 3:
                    modificarSucursal();
                    break;
                case 4:
                    buscarSucursal();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    public void agregarSucursal() {
        System.out.println("\n--- Agregar Sucursal ---");
        System.out.println("Descripcion: ");
        String descripcion = scanner.nextLine();
        System.out.println("Stock: ");
        String direccion = scanner.nextLine();
        String result = sucursalController.createSucursal(0, descripcion, direccion);
        System.out.println(result);
    }

    public void eliminarSucursal() {
        System.out.println("\n--- Eliminar Sucursal ---");
        System.out.println("ID del sucursal: ");
        int id_sucursal = leerEntero();
        String result = sucursalController.eliminarSucursal(id_sucursal);
        System.out.println(result);
    }

    public void modificarSucursal() {
        System.out.println("\n--- Modificar Sucursal ---");
        System.out.println("ID del sucursal: ");
        int id_sucursal = leerEntero();
        System.out.println("!!SI NO DESEA MODIFICAR UN CAMPO, SOLO PRESIONE ENTER!!");
        System.out.println("Descripción: ");
        String descripcion = scanner.nextLine();
        System.out.println("Dirección: ");
        String direccion = scanner.nextLine();
        String result = sucursalController.modificarSucursal(id_sucursal, descripcion, direccion);
        System.out.println(result);
    }

    public void buscarSucursal() {
        System.out.println("\n--- Buscar Sucursal ---");
        System.out.println("!!SI DESEA VER TODOS LOS SUCURSALS, SOLO PRESIONE ENTER!!");
        System.out.println("ID del sucursal: ");
        int id_sucursal = leerEntero();
        List<Sucursal> result = sucursalController.consultarSucursal(id_sucursal);
        for (Sucursal sucursal : result) {
            System.out.printf(sucursal.toString() + "\n");
        }
    }

    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
