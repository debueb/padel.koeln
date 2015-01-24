/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.db.dao;

import de.appsolve.padelcampus.db.model.Player;
import java.util.List;

/**
 *
 * @author dominik
 */
public interface PlayerDAOI extends GenericDAOI<Player>{
    
    public List<Player> findRegistered();
    
    public Player createOrUpdate(Player entity);
    
    public Player findByEmail(String email);
    
    public Player findByUUID(String UUID);

    public Player findByPasswordResetUUID(String UUID);
}
