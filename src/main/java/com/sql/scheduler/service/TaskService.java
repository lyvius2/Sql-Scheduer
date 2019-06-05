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

import java.util.List;
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

	public Job save(Job job) {
		boolean isConfirmMailingCase = false;
		int registerSeq = 1;
		String registerEmail = job.getRegUsername();
		if (job.getJobSeq() != 0) {
			Job prevJob = jobRepository.findById(job.getJobSeq()).get();
			if (!job.getTargetTable().equals(prevJob.getTargetTable())
					|| !job.getPerforming().equals(prevJob.getPerforming())
					|| !job.getConditional().equals(prevJob.getConditional())) {
				isConfirmMailingCase = true;
				registerSeq = (agreeRepository.getMaxRegisterSeqByJobSeq(job.getJobSeq()) + 1);
				registerEmail = job.getModUsername();
			}
		} else {
			isConfirmMailingCase = true;
		}

		Job resultJob = jobRepository.save(job);
		if (isConfirmMailingCase) saveCheckerDataAndSendingEmail(registerEmail, resultJob.getTargetTable(), resultJob.getJobSeq(), registerSeq);
		return resultJob;
	}

	public int countByGroupSeq(int groupSeq) {
		return (int)jobRepository.countByGroupSeq(groupSeq);
	}

	public List<Job> findAllByGroupSeq(int groupSeq) {
		return jobRepository.findAllByGroupSeqOrderByTaskSeqAsc(groupSeq);
	}

	private void saveCheckerDataAndSendingEmail(String registerEmail, String targetTable, int jobSeq, int registerSeq) {
		Admin register = adminRepository.findByUsername(registerEmail);
		String senderEmail = register.getEmail();
		Stream<Admin> checkers = adminRepository.findAllByType(AdminType.DEVELOPER).stream().filter(a -> !a.getEmail().equals(senderEmail));

		checkers.forEach(c -> {
			JobAgree jobAgree = new JobAgree();
			jobAgree.setJobSeq(jobSeq);
			jobAgree.setJobAgreeSeq(registerSeq);
			jobAgree.setUsername(c.getUsername());
			agreeRepository.save(jobAgree);
		});

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(senderEmail);
		message.setTo(checkers.map(a -> a.getEmail()).toArray(String[]::new));
		message.setSubject(targetTable + " 배치 작업에 대한 쿼리 확인 메일입니다.");
		message.setText("테스트 메일입니다.");
		sender.sendMail(message);
	}
}
