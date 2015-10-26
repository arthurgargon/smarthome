package com.gargon.smarthome.supradin.utils.supradinkeeper.entities.dao;

import com.gargon.smarthome.supradin.utils.supradinkeeper.entities.Sniff;
import org.hibernate.HibernateException;

/**
 *
 * @author gargon
 */
public class SniffDAO extends DAO{
    
        public Sniff createSniff(int src, int dst, int cmd, byte[] data, String interpretation)
            throws Exception {
        try {
            begin();
            Sniff user = new Sniff(src, dst, cmd, data, interpretation);
            getSession().save(user);
            commit();
            return user;
        } catch (HibernateException e) {
            rollback();
            throw new Exception("Could not create sniff" , e);
        }
    }

   /* public Sniff retrieveUser(String username) throws Exception {
        try {
            begin();
            Query q = getSession().createQuery("from User where name = :username");
            q.setString("username", username);
            User user = (User) q.uniqueResult();
            commit();
            return user;
        } catch (HibernateException e) {
            rollback();
            throw new Exception("Could not get user " + username, e);
        }
    }

    public void deleteUser( User user ) throws Exception {
        try {
            begin();
            getSession().delete(user);
            commit();
        } catch (HibernateException e) {
            rollback();
            throw new Exception("Could not delete user " + user.getName(), e);
        }
    }*/
    
}
