package com.demo.mongo.service;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.demo.mongo.common.DataUtil;
import com.demo.mongo.model.entity.mongo.Address;
import com.demo.mongo.model.entity.mongo.QUserMst;
import com.demo.mongo.model.entity.mongo.RoleDetail;
import com.demo.mongo.model.entity.mongo.UserMst;
import com.demo.mongo.repository.UserMstRepository;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserMstRepository userMstRepository;
//	private final UserMstRepositoryImpl userMstRepository;
	private final MongoTemplate mongoTemplate;
	private final DataUtil dataUtil;
	
	
	QUserMst  qUserMst = QUserMst.userMst;  
			
	
	private final int idx1 = 2;
	private final int idx2 = 200;
	private String desc = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
			+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest";
	
	
	
	
	
	
	
	public Object getUsers() {
		//모두 조회
		List<UserMst> list = userMstRepository.findAll();
		
		return list;
	}
	
	public Object getUserPaging() {
		
		
		PageRequest page = PageRequest.of(2, 30 , Sort.by("createDate").descending());
		LocalDateTime sdate = LocalDateTime.now().minusDays(5);
		LocalDateTime edate = LocalDateTime.now();//.minusDays(1)
		log.info("sdate = {}" , sdate);
		log.info("edate = {}" , edate);
		//모두 조회
		Page<UserMst> list = userMstRepository.findAll(
							qUserMst.createDate.after(sdate)
							.and(qUserMst.createDate.before(edate))
							,page);
		
		log.info("page list = {}" , list);
		
		Query query = new Query();
		
		return list;
	}
	
	//조회 get mg/user/{userId}
	public Object getUser(String userId) {
		//특정 조건 조회1
		List<UserMst> list1 = mongoTemplate.find(dataUtil.getQuery("userId",userId),UserMst.class);
		//특정 조건 조회2
		List<UserMst> list2 = userMstRepository.findByUserId(userId);
		
//		Criteria c = new Criteria("userId");
//		c.is(val);
//		
//		Criteria("title").isEqualTo("Moby-Dick")
//		  .and("price").lt(950)
		//gte 크거나같음 lte 작거나 같음 
		Query q = dataUtil.getQuery(Criteria.where("age").gte(300));//.and("userId").equals("user1")
		
		List<UserMst> list3 = mongoTemplate.find(q, UserMst.class);
		;
		
		
		//특정조건 조회 3 : querydsl >> findOne인 경우는 결과가 list인 경우 error
		Optional<UserMst> opt = 
				userMstRepository.findOne(
								 qUserMst.userId.eq("user1")
							.and(qUserMst.age.eq(300)));
		
		if(opt.isPresent()) {
			log.info("opt = {}",  opt.get().toString());
		}
		
		
		
		
		//특정조건 조회 4 : querydsl
		Iterable<UserMst> iter = 
				userMstRepository.findAll(
						    qUserMst.userId.eq("user1")
//						.or(qUserMst.userId.eq("user100"))
//						qUserMst.userId.in("user1","user100")
//						qUserMst.age.between(1, 300)
					   ,Sort.by("createDate").descending()
//					   ,Sort.by("createDate").ascending()
						);
		
		
		
		
		
		iter.forEach(x->{
			log.info("createDate = {} / userId = {}",x.getCreateDate(),  x.getUserId());
		});
		Iterator<UserMst> userInter =  iter.iterator();
//		while (userInter.hasNext()) {
//			log.info("userInter = {}",userInter.next());
//		}
	
		
//		//특정조건 조회 5 : querydsl
//		BooleanExpression whereClause =
//				 qUserMst.roleList.any().useYn.eq("Y")
//            .and(qUserMst.roleList.any().useEDate.gt(LocalDateTime.now().plusMonths(10)) );
//		Iterable<UserMst> inter2 =  userMstRepository.findAll(whereClause);
//
//		//특정조건 조회 6 : querydsl
//		BooleanExpression whereClaus3 =
//				 qUserMst.roleList.any().useYn.eq("Y")
//            .and(qUserMst.roleList.any().useEDate.between(LocalDateTime.now().plusDays(40),
//            		                                      LocalDateTime.now().plusDays(100)  ) );
//		Iterable<UserMst> inter3 =  userMstRepository.findAll(whereClaus3);
		
		
		
		/******** 대용량 쿼리 21만건 select start  *********/
		log.info("Mamager start 약 21만건 40MB로 추정 총 조회시간 2분정도 걸림. {}" , LocalDateTime.now());
		BooleanExpression whereClaus4 =
//				qUserMst.roleList.any().roleName.eq("MANAGER")
				qUserMst.roleList.any().roleName.eq("MANAGER")
//				 qUserMst.roleList.any().useYn.eq("Y")
//            .and(qUserMst.roleList.any().roleName.eq("MANAGER")  )
				;
		Iterable<UserMst> inter4 =  userMstRepository.findAll(whereClaus4);
		
		log.info("Mamager end {}" , LocalDateTime.now());
		
		List<Object>  itList11 =  dataUtil.iteratorToList(inter4);
		
		log.info("Mamager change {}" , LocalDateTime.now());
		/******** 대용량 쿼리 21만건 select end  *********/
		
		List<String> userList = Arrays.asList(
				 "user1","user10"
				,"user2","user20"
				,"user3","user30"
				,"user4","user40"
				,"user5","user50"
				,"user6","user60"
				,"user7","user70"
				,"user8","user80"
				,"user9","user90"
				);
		Iterable<UserMst> iter10 = 
				userMstRepository.findAll(
						    qUserMst.userId.in(userList)
//						.or(qUserMst.userId.eq("user100"))
//						qUserMst.userId.in("user1","user100")
//						qUserMst.age.between(1, 300)
					   ,Sort.by("createDate").descending()
//					   ,Sort.by("createDate").ascending()
						);
		List<Object>  itList =  dataUtil.iteratorToList(iter10);
		return itList;
	}
	
	
	public void getGroupbyUsers() {
		
		
		
		
	}
	
	//유저 저장
	public Object saveUser(UserMst user) {
		
		Address addr1 = Address.builder().addr("화성"+idx1).zipcode("16089").desc("test").addrDetail("203/2304").build();
		user = UserMst.builder()
				.userId("user"+idx1)
//				.address(addr1)
				.userName("윤정호"+idx1).birth("19990101").phone("01042467729").age(1)
				.build();
		
		Address addr2 = Address.builder().addr("화성"+idx2).zipcode("16089").desc("test").addrDetail("203/2304").build();
		UserMst user2 = UserMst.builder()
				.userId("user"+idx2)
//				.address(addr2)
				.birth("19990101").userName("윤정호"+idx2).phone("01042467729").age(1)
				.build();
		
		//저장방법 1
		UserMst r1 = userMstRepository.save(user);
		//저장밥법 2
		UserMst r2 = mongoTemplate.insert(user2, "UserMst");
		
		
		System.out.println("save r1 == "+r1);
		System.out.println("save r2 == "+r2);
		List<Object> result = Arrays.asList(r1,r2);
		
		return result;
	}
	
	List<String> cityList = Arrays.asList("서울","경기","인천","강원도","경상남도","경상북도","전라남도","전라북도","제주도","충청남도","충청북도");
	
	public Object saveUsers() {
		long cnt = userMstRepository.count();
		log.info("user cnt = {}",cnt);
		for (int i = 0; i < 10000; i++) {
			int idx = (int)Math.ceil(Math.random()*10);
			
			Address addr1 = Address.builder()
					.gubun("home")
					.city(cityList.get(idx))
					.addr("test addr")
					.addrDetail("test addr detail 203/2304")
					.zipcode("16089")
					.desc("test")
					.build();
			
			UserMst user = new UserMst();
			user.setUserId("user"+(cnt+i));
			user.getAddrList().add(addr1);
			user.setUserName("윤정호"+(cnt+i));
			user.setBirth("19990102");
			user.setPhone("01042467729");
			user.setAge((int)Math.random()*100);
			user.setDesc(desc);
			
			//role 삽입
			String []  roles = new String[] {"ADMIN","MANAGER","USER"};
			int    [] useDay = new int[]    {356 ,90 , 30};
			for (int j = 0; j < roles.length; j++) {
				
				RoleDetail r = new RoleDetail();
				r.setRoleName(roles[j]);
				r.setUseYn("Y");
				r.setUseSDate(LocalDateTime.now());
				r.setUseEDate(LocalDateTime.now().plusDays(useDay[j]));
				
				
				if(roles[j].equals("ADMIN") && user.getUserId().equals("user0") ) {
					user.getRoleList().add(r);
				}
				else if(roles[j].equals("MANAGER") &&  (user.getUserId().equals("user0") || user.getUserId().indexOf("user1") > -1 )) {
					user.getRoleList().add(r);
				}
				else if(roles[j].equals("USER")){
					user.getRoleList().add(r);
				}
			}
			
			UserMst r1 = userMstRepository.save(user);// 이거로 저장하면 localdatetime 에 utc에 우라리나 시차 더해줘야함 +9시간
//			UserMst r1 = mongoTemplate.insert(user, "UserMst");;//이거로 저장하면 localdate time 가 현재 시간으로 들어감.
		}
		
		return ResponseEntity.ok("success");
	}
	
	//수정
	public Object setUser() {
		
		Query query = dataUtil.getQuery("userId", "user1");
		Update update = new Update();
//			update.inc("age", 100);
		update.set("age", 300);
		UpdateResult u = mongoTemplate.updateFirst(query, update, UserMst.class);
		return ResponseEntity.ok(u);
	}
	
	
	//모든 유저 삭제
	public void allDeleteUser() {
		userMstRepository.deleteAll();
//		DeleteResult d1 = mongoTemplate.re;
	}
	
	//특정 유저 삭제
	public DeleteResult deleteUser(String userId) {
		//삭제방법 1
		DeleteResult d1 = mongoTemplate.remove(dataUtil.getQuery("userId",userId), "UserMst");
		return d1;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
