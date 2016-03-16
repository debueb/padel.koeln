package de.appsolve.padelcampus.db.dao;
;
import de.appsolve.padelcampus.db.dao.generic.GenericDAO;
import de.appsolve.padelcampus.db.model.Player;
import de.appsolve.padelcampus.constants.PaymentMethod;
import de.appsolve.padelcampus.db.dao.generic.BaseEntityDAO;
import java.util.List;
import org.joda.time.LocalDate;
import de.appsolve.padelcampus.db.model.Booking;
import java.util.HashMap;
import java.util.Map;
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
public class BookingBaseDAO extends BaseEntityDAO<Booking> implements BookingBaseDAOI{

   @Override
    public List<Booking> findBlockedBookings() {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(getGenericSuperClass(GenericDAO.class));
        criteria.add(Restrictions.isNotNull("blockingTime"));
        criteria.add(Restrictions.or(Restrictions.isNull("cancelled"), Restrictions.eq("cancelled", false)));
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return (List<Booking>) criteria.list();
    }

    @Override
    public void cancelBooking(Booking booking) {
        booking.setBlockingTime(null);
        booking.setCancelled(true);
        booking.setCancelReason("Session Timeout");
        saveOrUpdate(booking);
    }
}