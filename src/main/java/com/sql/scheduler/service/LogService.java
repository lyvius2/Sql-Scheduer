package com.sql.scheduler.service;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.entity.SystemLog;
import com.sql.scheduler.entity.TaskLog;
import com.sql.scheduler.model.Paging;
import com.sql.scheduler.repository.SystemLogRepository;
import com.sql.scheduler.repository.TaskLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LogService {
	@Autowired
	private SystemLogRepository systemLogRepository;

	@Autowired
	private TaskLogRepository taskLogRepository;

	public HashMap<String, Object> findByStatusOrderBy_idDesc(ResultStatus status, int currPageNo) {
		PageRequest pageRequest = PageRequest.of(currPageNo - 1, 10);
		Page<SystemLog> page = systemLogRepository.findByStatus(status, pageRequest);
		long count = systemLogRepository.countByStatus(status);
		return rtnPagingDataMap(page, currPageNo, count);
	}

	public TaskLog findTopByJobSeqOrderBy_idDesc(int jobSeq) {
		return taskLogRepository.findTopByJobSeq(jobSeq);
	}

	public HashMap<String, Object> findAllByOrderBy_idDesc(int currPageNo) {
		PageRequest pageRequest = PageRequest.of(currPageNo - 1, 10);
		Page<TaskLog> page = taskLogRepository.findAll(pageRequest);
		long count = taskLogRepository.count();
		return rtnPagingDataMap(page, currPageNo, count);
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
