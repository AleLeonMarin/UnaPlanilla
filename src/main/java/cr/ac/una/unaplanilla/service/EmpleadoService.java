package cr.ac.una.unaplanilla.service;

import cr.ac.una.unaplanilla.model.Empleado;
import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.util.EntityManagerHelper;
import cr.ac.una.unaplanilla.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Query;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmpleadoService {

    EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;

    public Respuesta getUsuario(String usuario, String clave) {
        try {
            Query qryUsuario = em.createNamedQuery("Empleado.findByUsuCla", Empleado.class);
            qryUsuario.setParameter("usuario", usuario);
            qryUsuario.setParameter("clave", clave);
            EmpleadoDto empleadoDto = new EmpleadoDto((Empleado) qryUsuario.getSingleResult());
            return new Respuesta(true, "", "", "Usuario", empleadoDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un usuario con las credenciales ingresadas.",
                    "getUsuario NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE,
                    "Ocurrio un error al consultar el usuario.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar el usuario.",
                    "getUsuario NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE,
                    "Error obteniendo el usuario [" + usuario + "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario.", "getUsuario " + ex.getMessage());
        }
    }

    public Respuesta getEmpleado(Long id) {
        try {
            Query qryEmpleado = em.createNamedQuery("Empleado.findByEmpId", Empleado.class);
            qryEmpleado.setParameter("id", id);
            EmpleadoDto empleadoDto = new EmpleadoDto((Empleado) qryEmpleado.getSingleResult());
            return new Respuesta(true, "", "", "Empleado", empleadoDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un empleado con el código ingresado.",
                    "getEmpleado NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE,
                    "Ocurrio un error al consultar el usuario.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar el empleado.",
                    "getEmpleado NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE,
                    "Error obteniendo el empleado [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el empleado.", "getEmpleado " + ex.getMessage());
        }
    }

    public Respuesta filtrarEmpleados(String cedula, String nombre, String primerApellido, String segundoApellido) {
        try {
            StringBuilder jpql = new StringBuilder("SELECT e FROM Empleado e WHERE 1=1");

            if (cedula != null && !cedula.trim().isEmpty()) {
                jpql.append(" AND e.cedula LIKE :cedula");
            }
            if (nombre != null && !nombre.trim().isEmpty()) {
                jpql.append(" AND e.nombre LIKE :nombre");
            }
            if (primerApellido != null && !primerApellido.trim().isEmpty()) {
                jpql.append(" AND e.primerApellido LIKE :primerApellido");
            }
            if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
                jpql.append(" AND e.segundoApellido LIKE :segundoApellido");
            }

            Query query = em.createQuery(jpql.toString(), Empleado.class);

            if (cedula != null && !cedula.trim().isEmpty()) {
                query.setParameter("cedula", "%" + cedula + "%");
            }
            if (nombre != null && !nombre.trim().isEmpty()) {
                query.setParameter("nombre", "%" + nombre + "%");
            }
            if (primerApellido != null && !primerApellido.trim().isEmpty()) {
                query.setParameter("primerApellido", "%" + primerApellido + "%");
            }
            if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
                query.setParameter("segundoApellido", "%" + segundoApellido + "%");
            }

            List<Empleado> empleados = query.getResultList();
            List<EmpleadoDto> empleadosDto = empleados.stream().map(EmpleadoDto::new).collect(Collectors.toList());

            return new Respuesta(true, "", "", "Empleados", empleadosDto);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error filtrando los empleados", ex);
            return new Respuesta(false, "Error filtrando los empleados.", "filtrarEmpleados " + ex.getMessage());
        }
    }



    public Respuesta guardarEmpleado(EmpleadoDto empleadoDto) {
        try {
            et = em.getTransaction();
            et.begin();
            Empleado empleado;
            if (empleadoDto.getId() != null && empleadoDto.getId() > 0) {
                empleado = em.find(Empleado.class, empleadoDto.getId());
                if (empleado == null) {
                    return new Respuesta(false, "No se encontró el empleado a modificar.",
                            "guardarEmpleado NoResultException");
                }
                empleado.actualizar(empleadoDto);
                empleado = em.merge(empleado);
            } else {
                empleado = new Empleado(empleadoDto);
                em.persist(empleado);
            }
            et.commit();
            return new Respuesta(true, "", "", "Empleado", new EmpleadoDto(empleado));

        } catch (Exception ex) {
            et.rollback();
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error guardando el empleado.", ex);
            return new Respuesta(false, "Error guardando el empleado.", "guardarEmpleado " + ex.getMessage());

        }
    }

    public Respuesta eliminarEmpleado(Long id) {
        try {
            et = em.getTransaction();
            et.begin();
            Empleado empleado;
            if (id != null && id > 0) {
                empleado = em.find(Empleado.class, id);
                if (empleado == null) {
                    return new Respuesta(false, "No se encontro en el empleado a eliminar",
                            "eliminarEmpleado noResultExeption");
                }

                em.remove(empleado);
            } else {
                return new Respuesta(false, "Favor consultar el empleado a eliminar", "");

            }
            et.commit();
            return new Respuesta(true, "", "");

        } catch (Exception ex) {
            et.rollback();
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error eliminando el empleado ", ex);
            return new Respuesta(false, "Error elimanando el empleado.", "eliminarEmpleado" + ex.getMessage());
        }
    }
}
