package jpabook.jpashop;

import jpabook.jpashop.controller.BookForm;
import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {
    
    @Autowired
    EntityManager em;
    
    @Test
    public void updateTest() throws Exception {
        em.find(Book)
    }
}
