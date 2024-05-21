/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.unaplanilla.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import cr.ac.una.unaplanilla.model.TipoPlanillaDto;
import cr.ac.una.unaplanilla.model.Empleado;
import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.model.TipoPlanilla;
import cr.ac.una.unaplanilla.util.Respuesta;
import cr.ac.una.unaplanilla.util.EntityManagerHelper;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.unaplanilla.util.EntityManagerHelper;
import cr.ac.una.unaplanilla.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Query;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TipoPlanillaService {

    EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;

    public Respuesta getTipoPlanilla(Long id) {
        try {
            Query qryTipoPlanilla = em.createNamedQuery("TipoPlanilla.findByTplaId", TipoPlanilla.class);
            qryTipoPlanilla.setParameter("tplaId", id);
            TipoPlanillaDto tipoPlanillaDto = new TipoPlanillaDto((TipoPlanilla) qryTipoPlanilla.getSingleResult());
            return new Respuesta(true, "", "", "TipoPlanilla", tipoPlanillaDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un tipo de planilla con el código ingresado.",
                    "getTipoPlanilla NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE,
                    "Ocurrio un error al consultar el tipo de planilla.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar el tipo de planilla.",
                    "getTipoPlanilla NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE,
                    "Error obteniendo el tipo de planilla [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el tipo de planilla.", "getTipoPlanilla " + ex.getMessage());
        }
    }

    public Respuesta guardarTipoPlanilla(TipoPlanillaDto tipoPlanillaDto) {
        try {
            et = em.getTransaction();
            et.begin();
            TipoPlanilla tipoPlanilla;
            if (tipoPlanillaDto.getId() != null && tipoPlanillaDto.getId() > 0) {
                tipoPlanilla = em.find(TipoPlanilla.class, tipoPlanillaDto.getId());
                if (tipoPlanilla == null) {
                    et.rollback();
                    return new Respuesta(false, "No se encontró el tipo de planilla a modificar.",
                            "guardarTipoPlanilla NoResultException");
                }
                tipoPlanilla.actualizarTipoPlanilla(tipoPlanillaDto);
                tipoPlanilla = em.merge(tipoPlanilla);
            } else {
                tipoPlanilla = new TipoPlanilla(tipoPlanillaDto);
                em.persist(tipoPlanilla);
            }
            et.commit();
            return new Respuesta(true, "", "", "TipoPlanilla", new TipoPlanillaDto(tipoPlanilla));
        } catch (Exception ex) {
            et.rollback();
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE,
                    "Error al guardar el tipo de planilla.", ex);
            return new Respuesta(false, "Error al guardar el tipo de planilla.",
                    "guardarTipoPlanilla " + ex.getMessage());
        }
    }


    public Respuesta eliminarTipoPlanilla(Long id) {
        try {
            et = em.getTransaction();
            et.begin();
            TipoPlanilla tipoPlanilla;
            if (id != null && id > 0) {
                tipoPlanilla = em.find(TipoPlanilla.class, id);
                if (tipoPlanilla == null) {
                    et.rollback();
                    return new Respuesta(false, "No se encrontró el tipo de planilla a eliminar.",
                            "eliminarTipoPlanilla NoResultException");
                }
                em.remove(tipoPlanilla);
            } else {
                et.rollback();
                return new Respuesta(false, "Debe cargar el tipo de planilla a eliminar.",
                        "eliminarTipoPlanilla NoResultException");
            }
            et.commit();
            return new Respuesta(true, "", "");
        } catch (Exception ex) {
            et.rollback();
            if (ex.getMessage().contains("ORA-02292")) {
                return new Respuesta(false,
                        "No se puede eliminar el tipo de planilla porque tiene relaciones con otros registros.",
                        "eliminarTipoPlanilla " + ex.getMessage());
            }
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE,
                    "Ocurrio un error al guardar el tipo de planilla.", ex);
            return new Respuesta(false, "Ocurrio un error al eliminar el tipo de planilla.",
                    "eliminarTipoPlanilla " + ex.getMessage());
        }
    }

}