/*******************************************************************************
 * Copyright (c) 2005, 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package org.iac.module.metrics.reporter;

import java.util.Map;
import java.util.Map.Entry;

import org.iac.module.metrics.Counter;
import org.iac.module.metrics.CounterMetric;
import org.iac.module.metrics.HistogramMetric;
import org.iac.module.metrics.TimerMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.iac.module.metrics.Histogram;
import org.iac.module.metrics.Timer;

/**
 * 使用Slf4j将Metrics打印到日志，默认logger name是"metrics"，可在构造函数中设定。
 * 
 * @author
 */
public class Slf4jReporter implements Reporter {
	private Logger reportLogger;

	public Slf4jReporter() {
		this("metrics");
	}

	public Slf4jReporter(String loggerName) {
		reportLogger = LoggerFactory.getLogger(loggerName);
	}

	@Override
	public void report(Map<String, Counter> counters, Map<String, Histogram> histograms, Map<String, Timer> timers) {
		for (Entry<String, Counter> entry : counters.entrySet()) {
			logCounter(entry.getKey(), entry.getValue().snapshot);
		}

		for (Entry<String, Histogram> entry : histograms.entrySet()) {
			logHistogram(entry.getKey(), entry.getValue().snapshot);
		}

		for (Entry<String, Timer> entry : timers.entrySet()) {
			logTimer(entry.getKey(), entry.getValue().snapshot);
		}

	}

	private void logCounter(String name, CounterMetric counter) {
		reportLogger.info("type=COUNTER, name={}, count={}, lastRate={}, meanRate={}", name, counter.totalCount,
				counter.lastRate, counter.meanRate);
	}

	private void logHistogram(String name, HistogramMetric histogram) {
		reportLogger.info("type=HISTOGRAM, name={}, min={}, max={}, mean={}{}", name, histogram.min, histogram.max,
				histogram.mean, buildPcts(histogram.pcts));
	}

	private void logTimer(String name, TimerMetric timer) {
		reportLogger.info("type=TIMER, name={}, count={}, lastRate={}, meanRate={}, min={}ms, max={}ms, mean={}ms",
				name, timer.counterMetric.totalCount, timer.counterMetric.lastRate, timer.counterMetric.meanRate,
				timer.histogramMetric.min, timer.histogramMetric.max, timer.histogramMetric.mean,
				buildPcts(timer.histogramMetric.pcts));
	}

	private String buildPcts(Map<Double, Long> pcts) {
		StringBuilder builder = new StringBuilder();

		for (Entry<Double, Long> entry : pcts.entrySet()) {
			builder.append(", ").append(entry.getKey()).append("%<=").append(entry.getValue()).append("ms");
		}

		return builder.toString();
	}
}
