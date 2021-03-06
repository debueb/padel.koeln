package de.appsolve.padelcampus.db.dao;

import de.appsolve.padelcampus.db.dao.generic.GenericDAO;
import de.appsolve.padelcampus.db.model.Event;
import de.appsolve.padelcampus.db.model.Game;
import de.appsolve.padelcampus.db.model.GameSet;
import de.appsolve.padelcampus.db.model.Participant;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

/**
 * @author dominik
 */
@Component
public class GameSetDAO extends GenericDAO<GameSet> implements GameSetDAOI {

    private static final Logger log = Logger.getLogger(GameSetDAO.class);


    @Override
    public List<GameSet> findByParticipant(Participant participant) {
        Criteria crit = getCriteria();
        crit.add(Restrictions.eq("participant", participant));
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        @SuppressWarnings("unchecked")
        List<GameSet> gameSets = (List<GameSet>) crit.list();
        return gameSets;
    }

    @Override
    public List<GameSet> findByGame(Game game) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("game", game);
        return findByAttributes(attributes);
    }

    @Override
    public List<GameSet> findByEvent(Event event) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("event", event);
        return findByAttributes(attributes);
    }

    @Override
    public List<GameSet> findBy(Game game, Participant participant) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("game", game);
        attributes.put("participant", participant);
        return findByAttributes(attributes);
    }

    @Override
    public GameSet findBy(Game game, Participant participant, Integer setNumber) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("game", game);
        attributes.put("participant", participant);
        attributes.put("setNumber", setNumber);
        List<GameSet> gameSets = findByAttributes(attributes);
        if (gameSets.size() == 1) {
            return gameSets.get(0);
        }
        if (gameSets.size() > 1) {
            log.warn("Expected 1 but found " + gameSets.size() + " GameSets for " + attributes.toString());
        }
        return null;
    }
}