package nuevo.grupo.spring.boot.proyecto.sowad.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nuevo.grupo.spring.boot.proyecto.sowad.models.dao.IAyudaDao;
import nuevo.grupo.spring.boot.proyecto.sowad.models.entity.Ayuda;
import nuevo.grupo.spring.boot.proyecto.sowad.models.entity.Institucion;
import nuevo.grupo.spring.boot.proyecto.sowad.models.entity.LineaDeAyuda;
import nuevo.grupo.spring.boot.proyecto.sowad.models.entity.Producto;

@RequiredArgsConstructor
@Service
public class AyudaServiceImpl implements IAyudaService{

	private final IAyudaDao AyudaDao; 

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional(readOnly=true)
	@Override
	public Ayuda findByIdAyuda(Long idAyuda) {
		return AyudaDao.findById(idAyuda).orElse(null);
	}

	@Transactional
	@Override
	public Ayuda saveAyuda(Ayuda ayuda) {
		ayuda.verificarPorcionesTotales();
		ayuda.verificarPrecioTotal();
		return AyudaDao.save(ayuda);
	}

	@Transactional
	@Override
	public void deleteAyuda(Long idAyuda) {
		AyudaDao.deleteById(idAyuda);
	}

	@Transactional(readOnly=true)
	@Override
	public Page<Ayuda> getAll(Pageable pageable) {
		return AyudaDao.findAll(pageable);
	}

	//BUSQEUDA AVANZAADADADADADADAD
	public static final String GREATER_THAN="greater";
	public static final String LESS_THAN="less";
	public static final String EQUAL="equal";
	@Transactional(readOnly=true)
	@Override
	public Page<Ayuda> getData(HashMap<String, Object> conditions, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ayuda> query= cb.createQuery(Ayuda.class);
		Root<Ayuda> root = query.from(Ayuda.class);
			
		List<Predicate> predicates = new ArrayList<Predicate>();
		conditions.forEach((field,value) ->
		{
			switch (field)
			{
				case "producto":  
					CriteriaQuery<LineaDeAyuda> criteriaLineaDeAyuda = cb.createQuery(LineaDeAyuda.class); 
					Root<LineaDeAyuda> rootCriteriaLineaDeAyuda = criteriaLineaDeAyuda.from(LineaDeAyuda.class); 
					//Producto
					CriteriaQuery<Producto> criteriaProducto = cb.createQuery(Producto.class); 
					Root<Producto> rootCriteriaProducto = criteriaProducto.from(Producto.class); 
					Predicate predicateProducto = cb.like(rootCriteriaProducto.get("nombre"), "%"+(String)value+"%");
					criteriaProducto.select(rootCriteriaProducto).where(predicateProducto);
					List<Long> myListIDsProducto = new ArrayList<Long> ();
					for (Producto u : entityManager.createQuery(criteriaProducto).getResultList()) {
						myListIDsProducto.add(u.getId());
					}
					Expression<Producto> expProducto = rootCriteriaProducto.get("id");
					Predicate predicateParaProducto = expProducto.in(myListIDsProducto); //DATO
					// 
					criteriaLineaDeAyuda.select(rootCriteriaLineaDeAyuda).where(predicateParaProducto);
					List<Long> myListIDsAyudas = new ArrayList<Long> ();
					for (LineaDeAyuda u : entityManager.createQuery(criteriaLineaDeAyuda).getResultList()) {
						myListIDsAyudas.add(u.getAyuda().getId());
					}
					Expression<Long> expAyudas = root.get("id");
					predicates.add(expAyudas.in(myListIDsAyudas));
					break;
				case "institucion": 
					CriteriaQuery<Institucion> criteria= cb.createQuery(Institucion.class); 
					Root<Institucion> rootCriteria = criteria.from(Institucion.class); 
					Predicate predicate1 = cb.like(rootCriteria.get("numero"), "%"+(String)value+"%");
					Predicate predicate2 = cb.like(rootCriteria.get("nombre"), "%"+(String)value+"%");
					Predicate OrPredicatesInstituciones = cb.or(predicate2, predicate1);
					criteria.select(rootCriteria).where(OrPredicatesInstituciones);
					List<Long> myListIDsInstituciones = new ArrayList<Long> ();
					for (Institucion u : entityManager.createQuery(criteria).getResultList()) {
						myListIDsInstituciones.add(u.getId());
					}
					Expression<Institucion> expInstituciones = root.get("institucion");
					predicates.add(expInstituciones.in(myListIDsInstituciones)); 
					break;
				case "porcionesTotales":
					String porcionesTotalesCondition=(String) conditions.get("porcionesTotalesCondicion");
					switch (porcionesTotalesCondition)
					{
						case GREATER_THAN:
							predicates.add(cb.greaterThan(root.<Integer>get(field),(Integer)value));
							break;
						case LESS_THAN:
							predicates.add(cb.lessThan(root.<Integer>get(field),(Integer)value));
							break;
						case EQUAL:
							predicates.add(cb.equal(root.<Integer>get(field),(Integer)value));
							break;
					}
					break;
				case "precioTotal":
					String precioTotalCondition=(String) conditions.get("precioTotalCondicion");
					switch (precioTotalCondition)
					{
						case GREATER_THAN:
							predicates.add(cb.greaterThan(root.<Float>get(field),(Float)value));
							break;
						case LESS_THAN:
							predicates.add(cb.lessThan(root.<Float>get(field),(Float)value));
							break;
						case EQUAL:
							predicates.add(cb.equal(root.<Float>get(field),(Float)value));
							break;
					}
					break;
				case "fechaDeLlegada":
					String fechaDeLlegadaCondition=(String) conditions.get("fechaDeLlegadaCondicion");
					switch (fechaDeLlegadaCondition)
					{
						case GREATER_THAN:
							predicates.add(cb.greaterThan(root.<Date>get(field),(Date)value));
							break;
						case LESS_THAN:
							predicates.add(cb.lessThan(root.<Date>get(field),(Date)value));
							break;
						case EQUAL:
							predicates.add(cb.equal(root.<Date>get(field),(Date)value));
	                        break;
					}
					break;
				case "fechaDeEnvio":
					String fechaDeEnvioCondition=(String) conditions.get("fechaDeEnvioCondicion");					
					switch (fechaDeEnvioCondition)
					{
						case GREATER_THAN:
							predicates.add(cb.greaterThan(root.<Date>get(field),(Date)value));
							break;
						case LESS_THAN:
							predicates.add(cb.lessThan(root.<Date>get(field),(Date)value));
							break;
						case EQUAL:
							predicates.add(cb.equal(root.<Date>get(field),(Date)value));
	                        break;
					}
					break;
				case "fechaDeRegistro":
					String fechaDeRegistroCondition=(String) conditions.get("fechaDeRegistroCondicion");					
					switch (fechaDeRegistroCondition)
					{
						case GREATER_THAN:
							predicates.add(cb.greaterThan(root.<Date>get(field),(Date)value));
							break;
						case LESS_THAN:
							predicates.add(cb.lessThan(root.<Date>get(field),(Date)value));
							break;
						case EQUAL:
							predicates.add(cb.equal(root.<Date>get(field),(Date)value));
	                        break;
					}
					break;
				}
			}); 
			query.where(cb.and(predicates.toArray( new Predicate[predicates.size()])));
				
			
			List<Ayuda> result = entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
			
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		    Root<Ayuda> pedidosRootCount = countQuery.from(Ayuda.class);
		    countQuery.select(cb.count(pedidosRootCount)).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		    
		    
		    Long count = entityManager.createQuery(countQuery).getSingleResult();
			Page<Ayuda> result1 = new PageImpl<>(result, pageable, count);
		    return  result1;
	}

	@Transactional(readOnly=true)
	@Override
	public int countAyudas() {
		return (int)AyudaDao.count();
	}

	@Override
	public Ayuda findFirstByOrderByIdDesc() {
		return AyudaDao.findFirstByOrderByIdDesc();
	}

}
