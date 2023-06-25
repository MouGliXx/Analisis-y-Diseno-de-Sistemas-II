package gestordeMensajes;

import conexion.Conexion;
import modelo.Mensaje;

public interface IGestordeMensajes {
    void crearConexion(Mensaje mensaje);
    void procesarMensaje (Conexion conexion,Mensaje mensaje);

}
