/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.db.dao;

import de.appsolve.padelcampus.db.dao.generic.BaseEntityDAOI;
import de.appsolve.padelcampus.db.model.Image;

/**
 *
 * @author dominik
 */
public interface ImageDAOI extends BaseEntityDAOI<Image>{
    
    Image findBySha256(String sha256);
}
