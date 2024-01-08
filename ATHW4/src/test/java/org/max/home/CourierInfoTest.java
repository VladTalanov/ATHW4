package org.max.home;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourierInfoTest extends AbstractTest{
    @Test
    @Order(1)
    void testAmountCouriersInDBHibernate()  {
        String sql = "FROM CourierInfoEntity";
        final Query query = getSession().createQuery(sql);
        //then
        Assertions.assertEquals(4, query.list().size());
    }

    @Test
    @Order(2)
    void testAmountCouriersInDBWithCar() throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE delivery_type='car'";
        Statement stmt  = getConnection().createStatement();
        int countCouriers = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countCouriers++;
        }
        //then
        Assertions.assertEquals(3, countCouriers);
    }

    @Test
    @Order(3)
    void testAddNewCourier() {
        Session session = getSession();
        final Query query1 = session.createSQLQuery("SELECT MAX(courier_id) FROM courier_info");
        int lastId = (int) query1.uniqueResult() + 1;

        CourierInfoEntity newCourier = new CourierInfoEntity();
        newCourier.setCourierId((short) lastId);
        newCourier.setFirstName("Alex");
        newCourier.setLastName("Black");
        newCourier.setPhoneNumber("+79652356030");
        newCourier.setDeliveryType("foot");

        session.beginTransaction();
        session.persist(newCourier);
        session.getTransaction().commit();

        final Query query2 = session.createSQLQuery("SELECT * FROM courier_info WHERE courier_id="+lastId)
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity addedCourier = (CourierInfoEntity)query2.uniqueResult();
        //then
        Assertions.assertNotNull(addedCourier);
        Assertions.assertEquals("Alex", addedCourier.getFirstName());
    }

    @Test
    @Order(4)
    void testDeleteNewCourier() {
        Session session = getSession();
        final Query query1 = session.createSQLQuery("SELECT * FROM courier_info WHERE last_name='Black'")
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity courierForDelete = (CourierInfoEntity) query1.uniqueResult();

        Assumptions.assumeTrue(courierForDelete != null);

        session.beginTransaction();
        session.delete(courierForDelete);
        session.getTransaction().commit();

        final Query query2 = session.createSQLQuery("SELECT * FROM courier_info WHERE last_name='Black'")
                .addEntity(CourierInfoEntity.class);

        CourierInfoEntity courierAfterDelete = (CourierInfoEntity) query2.uniqueResult();

        Assertions.assertNull(courierAfterDelete);
    }
    @Test
    @Order(5)
    void testChangeCourier() {
        Session session = getSession();
        final Query query1 = session.createSQLQuery("SELECT * FROM courier_info WHERE courier_id=4")
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity courier = (CourierInfoEntity) query1.uniqueResult();
        Assumptions.assumeTrue(courier != null);
        courier.setFirstName("Tom");
        session.beginTransaction();
        session.update(courier);
        session.getTransaction().commit();
        final Query query2 = session.createSQLQuery("SELECT * FROM courier_info WHERE courier_id=4")
                .addEntity(CourierInfoEntity.class);
        CourierInfoEntity courierForChange = (CourierInfoEntity) query2.uniqueResult();

        Assertions.assertEquals("Tom", courierForChange.getFirstName());
    }


}
