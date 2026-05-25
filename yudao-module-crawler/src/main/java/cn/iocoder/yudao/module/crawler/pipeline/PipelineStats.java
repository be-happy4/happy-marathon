package cn.iocoder.yudao.module.crawler.pipeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pipeline 处理统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PipelineStats {
    private int newCount;
    private int updatedCount;
    private int ignoredCount;

    public int total() {
        return newCount + updatedCount + ignoredCount;
    }

    public void addNew() { newCount++; }
    public void addUpdated() { updatedCount++; }
    public void addIgnored() { ignoredCount++; }
}
