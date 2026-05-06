package com.zeki.demo.progress.service;

import com.zeki.demo.progress.domain.ProgressInsights;
import com.zeki.demo.progress.domain.ProgressSummary;

public interface ProgressService {
    ProgressSummary getProgressSummary(String userEmail);
    ProgressInsights getProgressInsights(String userEmail);
}
