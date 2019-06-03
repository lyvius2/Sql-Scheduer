package com.sql.scheduler.service;

import com.sql.scheduler.code.AdminType;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.repository.AdminRepository;
import com.sql.scheduler.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private AdminRepository adminRepository;

	public Job findOne(int seq) {
		return jobRepository.findById(seq).get();
	}

	public Job save(Job job) {
		if (job.getJobSeq() != 0) {
			Job prevJob = jobRepository.findById(job.getJobSeq()).get();
			if (!job.getTargetTable().equals(prevJob.getTargetTable())
					|| !job.getPerforming().equals(prevJob.getPerforming())
					|| !job.getConditional().equals(prevJob.getConditional())) {
				List<Admin> admins = adminRepository.findAllByType(AdminType.DEVELOPER);
				Admin register = adminRepository.findByUsername(job.getModUsername());
				String senderEmail = register.getEmail();
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(senderEmail);
				message.setTo(admins.stream().filter(a -> !a.getEmail().equals(senderEmail)).map(a -> a.getEmail()).toArray(String[]::new));
				message.setSubject(job.getTargetTable() + " 배치 작업에 대한 쿼리 확인 메일입니다.");
				message.setText("테스트 메일입니다.");
				sendMail(message);
			}
		}
		return jobRepository.save(job);
	}

	public int countByGroupSeq(int groupSeq) {
		return (int)jobRepository.countByGroupSeq(groupSeq);
	}

	public List<Job> findAllByGroupSeq(int groupSeq) {
		return jobRepository.findAllByGroupSeqOrderByTaskSeqAsc(groupSeq);
	}

	public void sendMail(SimpleMailMessage message) {
		mailSender.send(message);
	}
}
