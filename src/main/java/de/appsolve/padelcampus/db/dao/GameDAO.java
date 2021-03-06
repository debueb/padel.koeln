package de.appsolve.padelcampus.db.dao;

import de.appsolve.padelcampus.constants.Gender;
import de.appsolve.padelcampus.db.dao.generic.GenericDAO;
import de.appsolve.padelcampus.db.model.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import java.util.*;

;

/**
 * @author dominik
 */
@Component
public class GameDAO extends GenericDAO<Game> implements GameDAOI {

    @Override
    public List<Game> findAllYoungerThanForGenderWithPlayers(LocalDate date, Gender gender) {

        Criteria criteria = getCriteria();
        criteria.createAlias("event", "e");
        criteria.add(Restrictions.gt("e.endDate", date));
        criteria.add(Restrictions.isNotEmpty("gameSets"));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        @SuppressWarnings("unchecked")
        List<Game> games = criteria.list();
        Iterator<Game> iterator = games.iterator();

        while (iterator.hasNext()) {
            Game game = iterator.next();
            for (Participant participant : game.getParticipants()) {
                if (participant instanceof Team) {
                    Team t = (Team) participant;
                    //check if game has been removed before to avoid IllegalStateException
                    if (games.contains(game) && !gender.equals(Gender.unisex) && (t.getGender() == null || !t.getGender().equals(gender))) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        return games;
    }

    @Override
    public List<Game> findByEventWithPlayers(Event event) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("event", event);
        return findAllFetchEagerlyWithAttributes(attributes, "participants.players");
    }

    @Override
    public List<Game> findByEvent(Event event) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("event", event);
        return findByAttributes(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Game> findByParticipant(Participant participant) {
        Criteria criteria = getFindByParticipantCriteria(participant);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Game> findByParticipantAndEvent(Participant participant, Event event) {
        Criteria criteria = getFindByParticipantCriteria(participant);
        criteria.add(Restrictions.eq("event", event));
        return criteria.list();
    }

    private List<Game> filterByParticipant(List<Game> games, Participant participant) {
        Iterator<Game> iterator = games.iterator();
        while (iterator.hasNext()) {
            Game game = iterator.next();
            if (!game.getParticipants().contains(participant)) {
                iterator.remove();
            }
        }
        return games;
    }

    @Override
    public Game findByIdFetchWithNextGame(Long id) {
        return super.findByIdFetchEagerly(id, "nextGame");
    }

    @Override
    public Game findByIdFetchWithEventAndNextGame(Long id) {
        return super.findByIdFetchEagerly(id, "event", "nextGame");
    }

    @Override
    public Game findByIdFetchWithTeamsAndScoreReporter(Long id) {
        return super.findByIdFetchEagerly(id, "participants.players", "scoreReporter");
    }

    @Override
    public Game findByIdFetchWithEventAndTeamsAndScoreReporter(Long id) {
        return super.findByIdFetchEagerly(id, "event", "participants.players", "scoreReporter");
    }

    @Override
    public List<Game> findByParticipantAndEventWithScoreOnly(Participant participant, Event event) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(GameSet.class, "gameset");
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        criteria.createAlias("gameset.game", "game");
        //criteria.add(Restrictions.eq("game", "gameset.Game"));
        criteria.add(Restrictions.eq("game.event", event));
        Customer customer = getCustomer();
        if (customer != null) {
            criteria.add(Restrictions.eq("customer", customer));
        }
        @SuppressWarnings("unchecked")
        List<GameSet> gameSets = (List<GameSet>) criteria.list();
        Set<Game> games = new HashSet<>();
        for (GameSet gameSet : gameSets) {
            games.add(gameSet.getGame());
        }
        return filterByParticipant(new ArrayList<>(games), participant);
    }

    private Criteria getFindByParticipantCriteria(Participant participant) {
        Criteria criteria = getCriteria();
        criteria.createAlias("participants", "p");
        criteria.add(Restrictions.like("p.id", participant.getId()));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria;
    }
}