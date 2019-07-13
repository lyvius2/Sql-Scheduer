package com.sql.scheduler.component;

import com.sql.scheduler.code.ResultStatus;
import com.sql.scheduler.entity.SystemLog;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;

@Component
public class LoggingUtil {
	public SystemLog createLogging(HttpServletRequest request, ResultStatus status, String username) {
		HashMap<String, Object> map = new HashMap<>();
		Enumeration params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String param = params.nextElement().toString();
			map.put(param, request.getParameter(param));
		}
		SystemLog systemLog = new SystemLog();
		systemLog.setStatus(status);
		systemLog.setMethod(request.getMethod());
		systemLog.setUsername(username);
		systemLog.setIp(request.getRemoteAddr());
		systemLog.setRequestPath(request.getServletPath());
		systemLog.setParams(map.toString());
		return systemLog;
	}

	public String createActionLogComment(HttpServletRequest request, String className, String methodName) {
		String comment = className + "." + methodName + " > ";
		switch (className) {
			case "TaskController":
				switch(methodName) {
					case "index":
						comment += "작업그룹 관리";
						break;
					case "group":
						if (request.getMethod().equals("GET")) {
							if (request.getServletPath().contains("/group/")) comment += "작업그룹 정보 불러오기 (JSON)";
							else comment += "작업그룹 관리";
						} else {
							comment += "작업그룹 저장";
						}
						break;
					case "removeGroup":
						comment += "작업그룹 삭제";
						break;
					case "task":
						if (request.getMethod().equals("GET")) comment += "작업 관리";
						else if (request.getMethod().equals("POST")) comment += "작업 정보 수정";
						else if (request.getMethod().equals("DELETE")) comment += "작업 삭제";
						break;
					case "cron":
						comment += "Cron 형식 유효성 체크 (JSON)";
						break;
					case "taskHistory":
						comment += "최근 스케줄 실행 이력 (JSON)";
						break;
				}
				break;
			case "LoginController":
				switch(request.getMethod()) {
					case "GET":
						comment += "로그인 화면 접속";
						break;
					case "POST":
						comment += "사용자 등록";
						break;
				}
				break;
			case "AdminController":
				switch(methodName) {
					case "admin":
						if (request.getMethod().equals("GET")) comment += "사용자 및 쿼리 승인 관리";
						else if (request.getMethod().equals("POST")) comment += "사용자 상태 변경";
						break;
					case "chgDeveloperStatus":
						comment += "사용자 유형 변경";
						break;
					case "decision":
						comment += "쿼리 승인/반려";
						break;
				}
				break;
			case "LogController":
				switch(methodName) {
					case "log":
						comment += "로그 기록 접속";
						break;
				}
		}
		return comment;
	}
}
