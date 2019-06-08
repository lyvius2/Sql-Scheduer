package com.sql.scheduler.service;

import com.sql.scheduler.code.AdminType;
import com.sql.scheduler.component.EmailSender;
import com.sql.scheduler.entity.Admin;
import com.sql.scheduler.entity.Job;
import com.sql.scheduler.entity.JobAgree;
import com.sql.scheduler.repository.AdminRepository;
import com.sql.scheduler.repository.JobAgreeRepository;
import com.sql.scheduler.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class TaskService {
	@Autowired
	private EmailSender sender;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobAgreeRepository agreeRepository;

	@Autowired
	private AdminRepository adminRepository;

	public Job findOne(int seq) {
		return jobRepository.findById(seq).get();
	}

	public HashMap<String, Object> save(Job job) throws InterruptedException {
		boolean isConfirmMailingCase = false;
		int registerSeq = 1;
		String administrator = job.getRegUsername();
		if (job.getJobSeq() != 0) {
			Job prevJob = jobRepository.findById(job.getJobSeq()).get();
			if (!job.getTargetTable().equals(prevJob.getTargetTable())
					|| !job.getPerforming().equals(prevJob.getPerforming())
					|| !job.getConditional().equals(prevJob.getConditional())) {
				isConfirmMailingCase = true;
				registerSeq = (agreeRepository.getMaxRegisterSeqByJobSeq(job.getJobSeq()) + 1);
				administrator = job.getModUsername();
			}
		} else {
			isConfirmMailingCase = true;
		}

		Job resultJob = jobRepository.save(job);
		HashMap<String, Object> result = new HashMap<>();
		result.put("resultJob", resultJob);
		result.put("isConfirmMailingCase", isConfirmMailingCase);
		result.put("registerSeq", registerSeq);
		result.put("administrator", administrator);
		return result;
	}

	public int countByGroupSeq(int groupSeq) {
		return (int)jobRepository.countByGroupSeq(groupSeq);
	}

	public List<Job> findAllByGroupSeq(int groupSeq) {
		return jobRepository.findAllByGroupSeqOrderByTaskSeqAsc(groupSeq);
	}

	public List<Admin> findCheckers(String administrator) {
		Admin register = adminRepository.findByUsername(administrator);
		String senderEmail = register.getEmail();
		List<Admin> checkers = adminRepository.findAllByType(AdminType.DEVELOPER).stream().filter(a -> !a.getEmail().equals(senderEmail)).collect(Collectors.toList());
		return checkers;
	}

	public JobAgree save(int jobSeq, int registerSeq, String checkerUsername) {
		JobAgree jobAgree = new JobAgree();
		jobAgree.setJobSeq(jobSeq);
		jobAgree.setRegisterSeq(registerSeq);
		jobAgree.setUsername(checkerUsername);
		return agreeRepository.save(jobAgree);
	}
/*
	public void sendingEmail(List<Admin> checkers) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(senderEmail);
		message.setTo(checkers.stream().map(a -> a.getEmail()).toArray(String[]::new));
		message.setSubject(targetTable + " 배치 작업에 대한 쿼리 확인 메일입니다.");
		message.setText("테스트 메일입니다.");
		sender.sendMail(message);
	}

	public void saveCheckerDataAndSendingEmail(String administrator, String targetTable, int jobSeq, int registerSeq) throws InterruptedException {
		Admin register = adminRepository.findByUsername(administrator);
		String senderEmail = register.getEmail();
		List<Admin> checkers = adminRepository.findAllByType(AdminType.DEVELOPER).stream().filter(a -> !a.getEmail().equals(senderEmail)).collect(Collectors.toList());

		for (Admin checker : checkers) {
			JobAgree jobAgree = new JobAgree();
			jobAgree.setJobSeq(jobSeq);
			jobAgree.setRegisterSeq(registerSeq);
			jobAgree.setUsername(checker.getUsername());
			agreeRepository.save(jobAgree);
			//Thread.sleep(1000);
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(senderEmail);
		message.setTo(checkers.stream().map(a -> a.getEmail()).toArray(String[]::new));
		message.setSubject(targetTable + " 배치 작업에 대한 쿼리 확인 메일입니다.");
		message.setText("테스트 메일입니다.");
		sender.sendMail(message);
	}
	/*
	private void saveCheckerDataAndSendingEmail(String administrator, String targetTable, int jobSeq, int registerSeq) throws InterruptedException {
		Admin register = adminRepository.findByUsername(administrator);
		String senderEmail = register.getEmail();
		List<Admin> checkers = adminRepository.findAllByType(AdminType.DEVELOPER).stream().filter(a -> !a.getEmail().equals(senderEmail)).collect(Collectors.toList());

		for (Admin checker : checkers) {
			JobAgree jobAgree = new JobAgree();
			jobAgree.setJobSeq(jobSeq);
			jobAgree.setRegisterSeq(registerSeq);
			jobAgree.setUsername(checker.getUsername());
			agreeRepository.save(jobAgree);
			//Thread.sleep(1000);
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(senderEmail);
		message.setTo(checkers.stream().map(a -> a.getEmail()).toArray(String[]::new));
		message.setSubject(targetTable + " 배치 작업에 대한 쿼리 확인 메일입니다.");
		message.setText("테스트 메일입니다.");
		sender.sendMail(message);
	}
	*/
}
