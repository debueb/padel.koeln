/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.utils;

import de.appsolve.padelcampus.db.dao.GameDAOI;
import de.appsolve.padelcampus.db.model.Event;
import de.appsolve.padelcampus.db.model.Game;
import de.appsolve.padelcampus.db.model.Participant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author dominik
 */
@Component
public class EventsUtil {
    
    @Autowired
    GameDAOI gameDAO;

    public SortedMap<Integer, List<Game>> getRoundGames(Event event) {
        SortedMap<Integer, List<Game>> roundGames= new TreeMap<>();
        
        for (Game game: event.getGames()){
            if (game.getRound()!=null){
                List<Game> games = roundGames.get(game.getRound());
                if (games == null){
                    games = new ArrayList<>();
                }
                games.add(game);
                roundGames.put(game.getRound(), games);
            }
        }
        return roundGames;
    }

    public SortedMap<Integer, List<Game>> getGroupGames(Event event) {
        SortedMap<Integer, List<Game>> groupGames= new TreeMap<>();
        
        for (Game game: event.getGames()){
            if (game.getGroupNumber()!=null){
                List<Game> games = groupGames.get(game.getGroupNumber());
                if (games == null){
                    games = new ArrayList<>();
                }
                games.add(game);
                groupGames.put(game.getGroupNumber(), games);
            }
        }
        return groupGames;
    }
    
    
    public void createKnockoutGames(Event event, List<Participant> participants) {
        //determine number of games per round
        int numGamesPerRound = Integer.highestOneBit(participants.size()-1);

        //determine seed positions
        List<Integer> seedingPositions = getSeedPositions(participants);


        //fill up empty spots with bye's
        for (int i=participants.size(); i< numGamesPerRound*2; i++){
            participants.add(null);
        }

        //create games
        int round=0;
        SortedMap<Integer, List<Game>> roundGames = new TreeMap<>();
        while (numGamesPerRound>=1){
            List<Game> games = new ArrayList<>();
            for (int i=0; i<numGamesPerRound; i++){
                Game game = new Game();
                game.setEvent(event);
                game.setRound(round);
                game = gameDAO.saveOrUpdate(game);

                if (round==0){
                    //set participants
                    Set<Participant> gameParticipants = new HashSet<>();
                    addParticipants(gameParticipants, participants.get(seedingPositions.get(i*2)));
                    addParticipants(gameParticipants, participants.get(seedingPositions.get(i*2+1)));
                    game.setParticipants(gameParticipants);
                } else {
                    //set game chain
                    List<Game> previousRoundGames = roundGames.get(round-1);
                    Game first = previousRoundGames.get(i*2);
                    Game second = previousRoundGames.get(i*2+1);
                    first.setNextGame(game);
                    second.setNextGame(game);
                    gameDAO.saveOrUpdate(first);
                    gameDAO.saveOrUpdate(second);


                    if (round==1){
                        //advance seeds that have bye's
                        if (first.getParticipants().size()==1){
                            game.setParticipants(new HashSet<>(first.getParticipants()));
                        }
                        if (second.getParticipants().size()==1){
                            Set<Participant> existingParticipants = game.getParticipants();
                            if (existingParticipants == null){
                                existingParticipants = new HashSet<>();
                            }
                            existingParticipants.addAll(new HashSet<>(second.getParticipants()));
                            game.setParticipants(existingParticipants);
                        }
                    }

                }

                if (numGamesPerRound == 1){
                    //TODO: when we are in the final, check if we also play for third place
                }

                game = gameDAO.saveOrUpdate(game);
                games.add(game);
            }
            roundGames.put(round, games);
            numGamesPerRound = numGamesPerRound/2;
            round++;
        }
    }
    
    private void addParticipants(Set<Participant> gameParticipants, Participant p) {
        if (p != null){
            gameParticipants.add(p);
        }
    }

    private List<Integer> getSeedPositions(List<Participant> participants) {
        Double numberOfDivisionRuns = Math.log(participants.size()) / Math.log(2)-1;
        List<Integer> seedingPositions = new ArrayList<>();
        seedingPositions.add(0);
        seedingPositions.add(1);
        for (int divisionRun=0; divisionRun<numberOfDivisionRuns; divisionRun++){
            int size = seedingPositions.size();
            List<Integer> newSeeedingPositions = new ArrayList<>();
            for (Integer position: seedingPositions){
                newSeeedingPositions.add(position);
                newSeeedingPositions.add(size*2-1-position);
            }
            seedingPositions = newSeeedingPositions;
        }
        return seedingPositions;
    }
    
}