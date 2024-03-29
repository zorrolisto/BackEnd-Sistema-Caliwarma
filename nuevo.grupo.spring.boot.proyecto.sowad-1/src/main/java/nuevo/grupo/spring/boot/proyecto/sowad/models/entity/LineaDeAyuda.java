package nuevo.grupo.spring.boot.proyecto.sowad.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Table(name="linea_de_ayuda")
public class LineaDeAyuda  implements Serializable{
    
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@NumberFormat(style = NumberFormat.Style.NUMBER)
	@Min(1)
	@Max(100000)
	@Setter @Getter
	private int porciones;

	@NotNull
	@ManyToOne
	@Setter @Getter
	private Producto producto;

	@ManyToOne
    @JoinColumn
	@Setter
    private Ayuda ayuda;

	@JsonIgnore
	public Ayuda getAyuda() {
		return ayuda;
	}
}
