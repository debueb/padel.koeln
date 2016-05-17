/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.db.dao.generic;

import de.appsolve.padelcampus.constants.Constants;
import de.appsolve.padelcampus.db.model.BaseEntityI;
import de.appsolve.padelcampus.db.model.Customer;
import de.appsolve.padelcampus.utils.GenericsUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author dominik
 * @param <T>
 */
@Repository
@Transactional
public abstract class BaseEntityDAO<T extends BaseEntityI> extends GenericsUtils<T> implements BaseEntityDAOI<T>  {
   
    private static final Logger LOG = Logger.getLogger(BaseEntityDAO.class);

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public T findById(Long id) {
        return entityManager.find(getGenericSuperClass(GenericDAO.class), id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {
        Criteria criteria = getCriteria();
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<T>) criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll(List<Long> ids) {
        Criterion[] criterion = new Criterion[ids.size()];
        for (int i=0; i<ids.size(); i++){
            criterion[i] = Restrictions.eq("id", ids.get(i));
        }
        Disjunction or = Restrictions.or(criterion);
        Criteria criteria = getCriteria();
        criteria.add(or);
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<T>) criteria.list();
    }

    @Override
    public T findFirst() {
        List<T> allConfigs = findAll();
        if (allConfigs.isEmpty()) {
            return null;
        }
        return allConfigs.get(0);
    }

    @Override
    public T saveOrUpdate(T entity) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(entity);
        return entity;
    }
    
    @Override
    public void delete(T entity) {
        Session session = entityManager.unwrap(Session.class);
        session.delete(entity);
    }

    @Override
    public void deleteById(Long id) {
        delete(findById(id));
    }

    @Override
    public void delete(List<T> entities) {
        for (T entity : entities) {
            deleteById(entity.getId());
        }
    }

    @Override
    public List<T> findByAttributes(Map<String, Object> attributeMap) {
        Criteria criteria = getCriteria();
        for (Map.Entry<String, Object> entry : attributeMap.entrySet()) {
            criteria.add(Restrictions.eq(entry.getKey(), entry.getValue()));
        }
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<T>) criteria.list();
    }
    
    @Override
    public T findByAttribute(String key, Object value) {
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(key, value);
        List<T> objects = findByAttributes(attributeMap);
        if (objects == null || objects.isEmpty()) {
            return null;
        }
        if (objects.size() != 1) {
            LOG.warn("Query for [type=" + getGenericSuperClassName(GenericDAO.class) + ", key=" + key + ", value=" + value + "] returned " + objects.size() + " results, expected 1.");
        }
        return objects.get(0);
    }
    
    @Override
    public T findByIdFetchEagerly(final long id, String... associations) {
        Criteria criteria = getCriteria();
        for (String association: associations){
            criteria.setFetchMode(association, FetchMode.JOIN);
        }
        criteria.add(Property.forName("id").eq(id));
        return (T) criteria.uniqueResult();
    }
    
    @Override
    public List<T> findAllFetchEagerly(String... associations){
        Criteria crit = getCriteria();
        for (String association: associations){
            crit.setFetchMode(association, FetchMode.JOIN);
        }
        //we only want unique results
        //see http://stackoverflow.com/questions/18753245/one-to-many-relationship-gets-duplicate-objects-whithout-using-distinct-why
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return crit.list();
    }
    
    @Override
    public List<T> findAllFetchEagerlyWithAttributes(Map<String,Object> attributeMap, String... associations){
        Criteria crit = getCriteria();
        for (String association: associations){
            crit.setFetchMode(association, FetchMode.JOIN);
        }
        //we only want unique results
        //see http://stackoverflow.com/questions/18753245/one-to-many-relationship-gets-duplicate-objects-whithout-using-distinct-why
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return crit.list();
    }
    
    protected Criteria getCriteria() {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(getGenericSuperClass(GenericDAO.class));
        return criteria;
    }
    
    protected Customer getCustomer(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        if (request!=null){
            HttpSession session = request.getSession();
            if (session!=null){
                Object object = session.getAttribute(Constants.SESSION_CUSTOMER);
                if (object instanceof Customer){
                    return (Customer) object;
                }
            }
        }
        return null;
    }
}
