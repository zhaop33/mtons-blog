package mblog.data.test;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import mblog.persist.dao.AuthMenuDao;
import mblog.persist.entity.AuthMenuPO;

public class AuthMenuDaoTest extends SpringTransactionalContextTests{
	
	@Autowired
	private AuthMenuDao authMenuDao;
	
	@Test
	public void testFindByParentId(){
		List<AuthMenuPO> list = authMenuDao.findByParentId(2L);
		System.out.println(list);
	}

}
