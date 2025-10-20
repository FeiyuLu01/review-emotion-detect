<template>
    <div ref="chartRef" class="chart"></div>
  </template>
  
  <script setup>
  // Lightweight ECharts wrapper for emotion bars
  import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
  import * as echarts from 'echarts/core'
  import { BarChart } from 'echarts/charts'
  import { GridComponent, TooltipComponent } from 'echarts/components'
  import { CanvasRenderer } from 'echarts/renderers'
  
  echarts.use([BarChart, GridComponent, TooltipComponent, CanvasRenderer])
  
  /** Props: data is an array like [{ label: 'joy', score: 0.23 }, ...] */
  const props = defineProps({
    data: { type: Array, default: () => [] },
  })
  
  const chartRef = ref(null)
  let chart = null
  
  function render() {
    if (!chart) return
    // Take top 8 by score desc
    const sorted = [...props.data].sort((a, b) => b.score - a.score).slice(0, 8)
    const labels = sorted.map(d => d.label)
    const values = sorted.map(d => +(d.score * 100).toFixed(2))
  
    chart.setOption({
      grid: { left: 8, right: 16, top: 16, bottom: 8, containLabel: true },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, valueFormatter: v => `${v}%` },
      xAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
      yAxis: { type: 'category', data: labels },
      series: [{
        type: 'bar',
        data: values,
        barWidth: 16,
        itemStyle: {
          borderRadius: [4, 4, 4, 4],
          color: (params) => {
            // nice gradient by index
            const idx = params.dataIndex
            const colors = [
              ['#7C3AED', '#06B6D4'],
              ['#22c55e', '#06B6D4'],
              ['#f59e0b', '#fb7185'],
              ['#14b8a6', '#8b5cf6'],
            ]
            const c = colors[idx % colors.length]
            return new echarts.graphic.LinearGradient(1, 0, 0, 0, [
              { offset: 0, color: c[0] },
              { offset: 1, color: c[1] },
            ])
          },
        },
      }],
    })
  }
  
  onMounted(() => {
    chart = echarts.init(chartRef.value)
    render()
    window.addEventListener('resize', onResize)
  })
  
  onBeforeUnmount(() => {
    window.removeEventListener('resize', onResize)
    chart?.dispose()
    chart = null
  })
  
  function onResize() { chart?.resize() }
  watch(() => props.data, render, { deep: true })
  </script>
  
  <style scoped>
  .chart {
    width: 100%;
    height: 280px; /* adjust as needed */
  }
  </style>