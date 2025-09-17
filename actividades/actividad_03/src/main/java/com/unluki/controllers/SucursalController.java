package com.unluki.controllers;

import com.unluki.models.Sucursal;
import com.unluki.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.util.ArrayList;
import java.util.List;

public class SucursalController {

    public String createSucursal(int id_sucursal, String descripcion, String direccion) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Sucursal sucursal = new Sucursal(id_sucursal, descripcion, direccion);
            session.beginTransaction();
            session.persist(sucursal);
            session.getTransaction().commit();
            return "Sucursal creada.";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error al intentar crear la sucursal.";
    }

    public String eliminarSucursal(int id_sucursal) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Sucursal sucursal = session.find(Sucursal.class, id_sucursal);
            if (sucursal != null) {
                session.remove(sucursal);
                session.getTransaction().commit();
                return "Sucursal eliminada.";
            }
            return "No se encontró la sucursal.";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error al intentar eliminar la sucursal.";
    }

    public String modificarSucursal(int id_sucursal, String descripcion, String direccion) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Sucursal sucursal = session.find(Sucursal.class, id_sucursal);
            if (sucursal != null) {
                if (!descripcion.isEmpty()) {
                    sucursal.setDescripcion(descripcion);
                }
                if(!direccion.isEmpty()) {
                    sucursal.setDireccion(direccion);
                }
                session.merge(sucursal);
                session.getTransaction().commit();
                return "Sucursal modificada.";
            }
            return "No se encontró la sucursal.";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error al intentar modificar la sucursal.";
    }

    public List<Sucursal> consultarSucursal(int id_sucursal) {

        List<Sucursal> sucursales = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            NativeQuery<Sucursal> query;
            if (id_sucursal == -1) {
                String sql = "SELECT * FROM sucursal";
                query = session.createNativeQuery(sql, Sucursal.class);
            } else {
                String sql = "SELECT * FROM sucursal WHERE id_sucursal = :id_suc";
                query = session.createNativeQuery(sql, Sucursal.class);
                query.setParameter("id_suc", id_sucursal);
            }
            sucursales = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sucursales;
    }

}
