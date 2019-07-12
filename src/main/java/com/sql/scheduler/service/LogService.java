package com.sql.scheduler.service;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.entity.DeleteLog;
import com.sql.scheduler.entity.SystemLog;
import com.sql.scheduler.entity.TaskLog;
import com.sql.scheduler.model.Paging;
import com.sql.scheduler.repository.DeleteLogRepository;
import com.sql.scheduler.repository.SystemLogRepository;
import com.sql.scheduler.repository.TaskLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

@Service
public class LogService {
	@Autowired
	private MongoOperations mongoOperations;

	@Autowired
	private SystemLogRepository systemLogRepository;

	@Autowired
	private TaskLogRepository taskLogRepository;

	@Autowired
	private DeleteLogRepository deleteLogRepository;

	public HashMap<String, Object> findAllSystemLog(int currPageNo) {
		PageRequest pageRequest = PageRequest.of(currPageNo - 1, 10);
		Page<SystemLog> page = systemLogRepository.findAllByOrderByBeginTimeDesc(pageRequest);
		long count = systemLogRepository.count();
		return rtnPagingDataMap(page, currPageNo, count);
	}

	public HashMap<String, Object> findByStatusSystemLog(ResultStatus status, int currPageNo) {
		PageRequest pageRequest = PageRequest.of(currPageNo - 1, 10);
		Query query = new Query(Criteria.where("status").is(status.name())).with(Sort.by(new Order(Sort.Direction.DESC, "beginTime"))).with(pageRequest);
		List<SystemLog> list = mongoOperations.find(query, SystemLog.class, "systemLog");
		long count = mongoOperations.count(query, SystemLog.class);
		Page<SystemLog> page = new PageImpl<>(list, pageRequest, count);
		return rtnPagingDataMap(page, currPageNo, count);
	}

	public TaskLog findTopByJobSeq(int jobSeq) {
		return taskLogRepository.findTopByJobSeq(jobSeq);
	}

	public HashMap<String, Object> findAllTaskLog(int currPageNo) {
		PageRequest pageRequest = PageRequest.of(currPageNo - 1, 10);
		Page<TaskLog> page = taskLogRepository.findAllByOrderByBeginTimeDesc(pageRequest);
		long count = taskLogRepository.count();
		return rtnPagingDataMap(page, currPageNo, count);
	}

	public int removeLog(String flag) {
		long count = 0;
		switch(flag) {
			case "task":
				count = taskLogRepository.count();
				taskLogRepository.deleteAll();
				break;
			case "system":
				count = systemLogRepository.count();
				systemLogRepository.deleteAll();
				break;
		}
		return (int)count;
	}

	@Transactional
	public List<DeleteLog> findAllDeleteLog() {
		return deleteLogRepository.findByOrderByDtDesc();
	}

	private HashMap<String, Object> rtnPagingDataMap(Page page, int currPageNo, long count) {
		Paging paging = new Paging(currPageNo, 10);
		paging.setNumberOfRows((int) count);
		paging.Paging();

		HashMap<String, Object> map = new HashMap<>();
		map.put("list", page.getContent());
		map.put("paging", paging);
		return map;
	}
}
