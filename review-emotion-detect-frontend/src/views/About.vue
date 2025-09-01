<template>
  <div class="about container">
    <!-- ===== Hero: product intro + mission ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <a-col :xs="24" :md="14">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="2" style="margin: 0 0 8px 0;">
            About MoodLens
          </a-typography-title>
          <a-typography-paragraph type="secondary" style="margin-bottom: 12px;">
            MoodLens is a lightweight tool that surfaces the emotional tone of short texts.
            By making emotions visible and suggesting constructive alternatives, it helps keep
            online conversations clearer, kinder, and more effective.
          </a-typography-paragraph>

          <div class="badges">
            <a-tag color="blue">Gentle guidance</a-tag>
            <a-tag color="green">Actionable</a-tag>
            <a-tag color="purple">Privacy-first</a-tag>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="10">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="5" style="margin: 0 0 8px 0;">Our mission</a-typography-title>
          <a-typography-title :level="4" style="margin: 0 0 12px 0;">
            See the emotion — don’t be led by it
          </a-typography-title>
          <ul class="bullet">
            <li>Detect emotional tendencies quickly.</li>
            <li>Offer rewrites that keep meaning while softening tone.</li>
            <li>Improve communication without storing your text.</li>
          </ul>
        </a-card>
      </a-col>
    </a-row>

    <!-- ===== What it can do & How it works ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <a-col :xs="24" :md="14">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin: 0 0 12px 0;">
            What can MoodLens do?
          </a-typography-title>

          <div class="feature">
            <div class="feature-title">Identify emotional direction</div>
            <div class="feature-desc">
              Extract key emotions (e.g., positive, negative, surprise) with proportions and a dominant tone.
            </div>
          </div>

          <div class="feature">
            <div class="feature-title">Suggest better phrasing</div>
            <div class="feature-desc">
              Provide neutral / positive / polite rewrites that keep your meaning and reduce friction.
            </div>
          </div>

          <div class="feature">
            <div class="feature-title">Explain likely impact</div>
            <div class="feature-desc">
              Plain-language hints about how readers may feel, so you can decide whether to adjust.
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="10">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin: 0 0 12px 0;">
            How it works
          </a-typography-title>

          <div class="steps">
            <div class="step">
              <div class="dot">1</div>
              <div class="step-body">
                <div class="step-title">Paste your text</div>
                <div class="step-desc">A review, comment, or short paragraph.</div>
              </div>
            </div>
            <div class="step">
              <div class="dot">2</div>
              <div class="step-body">
                <div class="step-title">Local/controlled inference</div>
                <div class="step-desc">Processed in session or on a controlled server. No content retention.</div>
              </div>
            </div>
            <div class="step">
              <div class="dot">3</div>
              <div class="step-body">
                <div class="step-title">Visualize results</div>
                <div class="step-desc">Charts and a short summary of emotion distribution.</div>
              </div>
            </div>
            <div class="step">
              <div class="dot">4</div>
              <div class="step-body">
                <div class="step-title">Get suggestions</div>
                <div class="step-desc">Tone-aware rewrites that avoid over-editing your voice.</div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- ===== Privacy & Responsible Use + Scope ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <a-col :xs="24" :md="14">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin: 0 0 12px 0;">
            Privacy & Responsible Use
          </a-typography-title>

          <a-descriptions bordered :column="1">
            <a-descriptions-item label="Data minimization">
              Your text is used only for the current analysis. We don’t use it for training or long-term storage.
            </a-descriptions-item>
            <a-descriptions-item label="Explainability">
              We describe results in clear language and note uncertainty or limits when relevant.
            </a-descriptions-item>
            <a-descriptions-item label="No over-automation">
              Suggestions are guidance, not decisions. You remain the author — we avoid “speaking on your behalf”.
            </a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="10">
        <a-card class="about-card info-card" :bordered="false">
          <a-typography-title :level="4" style="margin: 0 0 8px 0;">
            Scope & limitations
          </a-typography-title>
          <a-typography-paragraph style="margin: 0;">
            Emotion analysis is statistical and can be affected by context, sarcasm, and ambiguity.
            For high-stakes cases (e.g., PR, safety), combine with human review.
          </a-typography-paragraph>
        </a-card>
      </a-col>
    </a-row>

    <!-- ===== Team & Contact ===== -->
    <a-row :gutter="[16, 16]" class="section">
      <a-col :xs="24" :md="14">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin: 0 0 12px 0;">
            Who we are
          </a-typography-title>
          <a-typography-paragraph type="secondary">
            We’re a small team focused on human-AI collaboration and digital citizenship. Our backgrounds
            span interaction design, NLP, and data visualization. We aim to balance usability and responsibility.
          </a-typography-paragraph>
          <div class="badges">
            <a-tag>Human-AI Interaction</a-tag>
            <a-tag>Responsible AI</a-tag>
            <a-tag>Data Visualization</a-tag>
            <a-tag>Product Thinking</a-tag>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="10">
        <a-card class="about-card" :bordered="false">
          <a-typography-title :level="4" style="margin: 0 0 12px 0;">
            Contact & feedback
          </a-typography-title>
          <a-typography-paragraph type="secondary" style="margin-bottom: 12px;">
            Got ideas, collaboration requests, or want to use MoodLens in teaching/research?
          </a-typography-paragraph>
          <div class="contact-actions">
            <a-button type="primary" href="mailto:hello@moodlens.example" target="_blank">Email us</a-button>
            <a-button href="/#try" type="default">Try MoodLens</a-button>
          </div>
          <a-typography-paragraph type="secondary" style="margin-top: 10px;">
            We read every message and use feedback to improve — without collecting personal content.
          </a-typography-paragraph>
        </a-card>
      </a-col>
    </a-row>

    <!-- ===== FAQ ===== -->
    <a-card class="about-card section" :bordered="false">
      <a-typography-title :level="4" style="margin: 0 0 12px 0;">FAQ</a-typography-title>
      <a-collapse ghost expand-icon-position="end">
        <a-collapse-panel key="1" header="Why do some rewrites look very similar to my original?">
          To preserve meaning and voice, the model makes minimal wording changes unless the tone is strong.
          Try the “polite” or “positive” tone for a more noticeable rewrite.
        </a-collapse-panel>
        <a-collapse-panel key="2" header="Can I clear my analysis results?">
          Yes. Use “Clear” on the Analyze page. Nothing is stored after your session.
        </a-collapse-panel>
        <a-collapse-panel key="3" header="Which languages are supported?">
          English works best. Other languages may work but can be less accurate.
        </a-collapse-panel>
      </a-collapse>
    </a-card>
  </div>
</template>

<script setup>
// 当前页不需要脚本逻辑；保留空壳以便后续扩展
</script>

<style scoped>
/* ===== Layout spacing ===== */
.section {
  margin-bottom: 24px; /* 区块之间的间距 */
}

.about-card {
  margin-bottom: 24px;   /* 卡片之间的统一间距（关键！） */
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(2, 6, 23, 0.06);
}

/* 信息提示样式（右上角 Scope 卡片） */
.info-card {
  border-left: 4px solid #67e8f9;
  background: linear-gradient(#fff, #fff) padding-box,
              linear-gradient(135deg, #22d3ee, #a78bfa) border-box;
  box-shadow: 0 8px 22px rgba(2, 6, 23, 0.08);
}

/* 文本与列表 */
.bullet {
  margin: 0;
  padding-left: 18px;
  color: #334155;
}
.bullet li { margin: 6px 0; }

/* 功能块 */
.feature { margin-bottom: 14px; }
.feature-title {
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}
.feature-desc { color: #475569; }

/* 步骤垂直排版 */
.steps { display: grid; gap: 12px; }
.step { display: grid; grid-template-columns: 28px 1fr; gap: 10px; align-items: start; }
.dot {
  width: 28px; height: 28px; border-radius: 50%;
  display: grid; place-items: center;
  font-weight: 700; font-size: 13px; color: #0f172a;
  background: #eef2ff; border: 1px solid #c7d2fe;
}
.step-title { font-weight: 600; color: #0f172a; }
.step-desc  { color: #64748b; }

/* 小徽章组 */
.badges { display: flex; gap: 8px; flex-wrap: wrap; }

/* 联系方式按钮 */
.contact-actions { display: flex; gap: 10px; flex-wrap: wrap; }
</style>