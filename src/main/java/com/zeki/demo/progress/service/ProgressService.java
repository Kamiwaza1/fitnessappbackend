package com.zeki.demo.progress.service;

import com.zeki.demo.progress.domain.Progress;

public interface ProgressService {
    Progress getProgress(String userEmail);
}
