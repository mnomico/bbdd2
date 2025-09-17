package com.unluki.controllers;

import com.unluki.models.Articulo;
import com.unluki.utils.HibernateUtil;
import jakarta.persistence.*;
import org.hibernate.*;
import org.hibernate.query.NativeQuery;
import java.util.ArrayList;
import java.util.List;


public class ArticuloController {

    public String createArticulo(int id_articulo, String descripcion, int stock) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Articulo articulo = new Articulo(id_articulo, descripcion, stock);
            session.beginTransaction();
            session.persist(articulo);
            session.getTransaction().commit();
            return("Articulo creado.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ("Error al intentar crear el articulo.");
    }

    public String eliminarArticulo(int id_articulo) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Articulo articulo = session.find(Articulo.class, id_articulo);
            if (articulo != null) {
                session.remove(articulo);
                session.getTransaction().commit();
                return("Articulo eliminado.");
            }
            return("No se encontro el articulo.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ("Error al intentar eliminar el articulo.");
    }

    public String modificarArticulo(int id_articulo, String descripcion, int stock) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Articulo articulo = session.find(Articulo.class, id_articulo);
            if (articulo != null) {
                if (!descripcion.isEmpty()) {
                    articulo.setDescripcion(descripcion);
                }
                if (stock != -1) {
                    articulo.setStock(stock);
                }
                session.merge(articulo);
                session.getTransaction().commit();
                return("Articulo modificado.");
            }
            return("No se encontro el articulo.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ("Error al intentar modificar el articulo.");
    }

    public List<Articulo> consultarArticulo(int id_articulo) {

        List<Articulo> articulos = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            NativeQuery<Articulo> query;
            if (id_articulo == -1) {
                String sql = "SELECT * FROM articulo";
                query = session.createNativeQuery(sql, Articulo.class);
            } else {
                String sql = "SELECT * FROM articulo WHERE id_articulo = :id_art";
                query = session.createNativeQuery(sql, Articulo.class);
                query.setParameter("id_art", id_articulo);
            }
            articulos = query.getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al intentar consultar articulos.");
        }

        return articulos;
    }
}
