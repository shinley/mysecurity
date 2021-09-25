package com.shinley.mysecurity.exception;

import com.shinley.mysecurity.config.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class DuplicateProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/duplicate");
    public DuplicateProblem(String message) {
        super(TYPE, "发现重复数据", Status.CONFLICT, message);
    }
}
