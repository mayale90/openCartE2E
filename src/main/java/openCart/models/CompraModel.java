package openCart.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompraModel {
    private String idCaso;
    private String usuario;
    private String productos;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String codPostal;
    private String pais;
    private String region;
    private String delivery;
    private String pago;
}
