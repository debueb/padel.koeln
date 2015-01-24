package de.appsolve.padelcampus.db.dao;
;
import de.appsolve.padelcampus.constants.PaymentMethod;
import java.util.List;
import org.joda.time.LocalDate;
import de.appsolve.padelcampus.db.model.Booking;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

/**
 *
 * @author dominik
 */
@Component
public class BookingDAO extends GenericDAO<Booking> implements BookingDAOI{

    @Override
    public Booking findByUUID(String UUID) {
        return findByAttribute("UUID", UUID);
    }    

    @Override
    public List<Booking> findBlockedBookingsForDate(LocalDate date) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(getGenericSuperClass(GenericDAO.class));
        criteria.add(Restrictions.eq("bookingDate", date));
        criteria.add(Restrictions.isNotNull("blockingTime"));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<Booking>) criteria.list();
    }

    @Override
    public List<Booking> findBlockedBookings() {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(getGenericSuperClass(GenericDAO.class));
        criteria.add(Restrictions.isNotNull("blockingTime"));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<Booking>) criteria.list();
    }

    @Override
    public List<Booking> findBetween(LocalDate startDate, LocalDate endDate) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(getGenericSuperClass(GenericDAO.class));
        criteria.add(Restrictions.ge("bookingDate", startDate));
        criteria.add(Restrictions.le("bookingDate", endDate));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<Booking>) criteria.list();
    }

    @Override
    public List<Booking> findReservations() {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(getGenericSuperClass(GenericDAO.class));
        criteria.add(Restrictions.eq("paymentMethod", PaymentMethod.Reservation));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<Booking>) criteria.list();
    }
}