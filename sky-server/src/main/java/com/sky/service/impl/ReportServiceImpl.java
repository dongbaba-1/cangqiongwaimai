package com.sky.service.impl;

import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        return null;
    }
}
