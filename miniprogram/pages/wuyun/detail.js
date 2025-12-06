Page({
  data: {
    statusBarHeight: 44,
    wuyun: {
      year: 2025,
      ganzhi: '乙巳',
      nayin: '覆灯火',
      wuyun: '金运不及',
      sitian: '厥阴风木',
      zaiquan: '少阳相火',
      climate: '上半年风气主令，易动肝阳；秋季金气肃降，易收敛过度',
      healthTips: [
        '上半年应顺时调理，防范肝阳上亢',
        '保持情绪平和，适当户外活动',
        '下半年炎热继续，需继续防暑'
      ]
    },
    // 六气客气循环（三阴三阳）
    sixQiClientCycle: [
      {
        number: 1,
        season: '立春 ~ 小满',
        name: '厥阴风木',
        type: 'sitian'
      },
      {
        number: 2,
        season: '茆穗 ~ 小暗',
        name: '少阴君火',
        type: 'sitian'
      },
      {
        number: 3,
        season: '谷雨 ~ 立夏',
        name: '太阴湿土',
        type: 'sitian'
      },
      {
        number: 4,
        season: '小满 ~ 夏至',
        name: '少阳相火',
        type: 'zaiquan'
      },
      {
        number: 5,
        season: '大暑 ~ 立秋',
        name: '阳明燥金',
        type: 'zaiquan'
      },
      {
        number: 6,
        season: '处暑 ~ 霜降',
        name: '太阳寒水',
        type: 'zaiquan'
      }
    ]
  },
  onLoad(options) {
    try {
      const sys = wx.getWindowInfo();
      if (sys && sys.statusBarHeight) {
        this.setData({ statusBarHeight: sys.statusBarHeight });
      }
    } catch (e) {}

    // 如果从首页传递了数据，则使用传递的数据
    if (options.wuyun) {
      const wuyunData = JSON.parse(decodeURIComponent(options.wuyun));
      
      this.setData({
        wuyun: wuyunData
      });
    }

    // SVG已使用，Canvas绘制已省略
    // if (typeof canvas !== 'undefined') {
    //   this.drawSixQiDiagram();
    // }
  },
  drawSixQiDiagram() {
    const query = wx.createSelectorQuery();
    query.select('#sixQiCanvas')
      .fields({ node: true, size: true })
      .exec((res) => {
        if (!res || !res[0] || !res[0].node) {
          console.error('Canvas 获取失败');
          return;
        }
        
        const canvas = res[0].node;
        const ctx = canvas.getContext('2d');
        
        // 设置实际尺寸（不使用画布尺寸）
        const width = 280;
        const height = 280;
        const dpr = wx.getWindowInfo().pixelRatio || 2;
        
        canvas.width = width * dpr;
        canvas.height = height * dpr;
        ctx.scale(dpr, dpr);
        
        const centerX = width / 2;
        const centerY = height / 2;
        
        // 清空背景
        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, width, height);
        
        // 半径设置
        const r1 = 25;    // 中心小圆 - 年支
        const r2 = 65;    // 中间圆 - 主气
        const r3 = 105;   // 外圈 - 客气
        
        // 三阴气数据（司天）
        const yinData = [
          '厥阴\n风\n木',
          '少阴\n君\n火',
          '太阴\n湿\n土'
        ];
        
        // 三阳气数据（在泉）
        const yangData = [
          '少阳\n相\n火',
          '阳明\n燥\n金',
          '太阳\n寒\n水'
        ];
        
        // 节气数据
        const seasonData = [
          '立春~小满',
          '雨水~惊蛰',
          '清明~立夏',
          '小满~夏至',
          '大暑~立秋',
          '处暑~霜降'
        ];
        
        // 1. 绘制最内小圆 - 年支
        ctx.fillStyle = '#f0f0f0';
        ctx.beginPath();
        ctx.arc(centerX, centerY, r1, 0, Math.PI * 2);
        ctx.fill();
        
        // 中心年支文字
        ctx.fillStyle = '#333333';
        ctx.font = 'bold 14px Arial';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(this.data.wuyun.ganzhi, centerX, centerY);
        
        // 2. 绘制中间圆 - 主气（司天为前3个，在泉为后3个）
        ctx.strokeStyle = '#1890ff';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.arc(centerX, centerY, r2, 0, Math.PI * 2);
        ctx.stroke();
        
        // 中间圆的六个气点
        for (let i = 0; i < 6; i++) {
          const angle = (Math.PI * 2 / 6) * i - Math.PI / 2;
          const x = centerX + Math.cos(angle) * r2;
          const y = centerY + Math.sin(angle) * r2;
          
          // 司天用蓝色标记（前3个），在泉用橙色标记（后3个）
          const isSitian = i < 3;
          ctx.fillStyle = isSitian ? '#e6f7ff' : '#fff7e6';
          ctx.beginPath();
          ctx.arc(x, y, 12, 0, Math.PI * 2);
          ctx.fill();
          
          // 圆边框
          ctx.strokeStyle = isSitian ? '#1890ff' : '#faad14';
          ctx.lineWidth = 1.5;
          ctx.beginPath();
          ctx.arc(x, y, 12, 0, Math.PI * 2);
          ctx.stroke();
          
          // 中间圆显示三阴三阳气
          ctx.fillStyle = '#333333';
          ctx.font = 'bold 9px Arial';
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          const qiData = isSitian ? yinData[i] : yangData[i - 3];
          const lines = qiData.split('\n');
          for (let j = 0; j < lines.length; j++) {
            ctx.fillText(lines[j], x, y - 3 + j * 6);
          }
        }
        
        // 3. 绘制外圆 - 客气
        ctx.strokeStyle = '#faad14';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.arc(centerX, centerY, r3, 0, Math.PI * 2);
        ctx.stroke();
        
        // 外圆的六个气点（显示节气）
        for (let i = 0; i < 6; i++) {
          const angle = (Math.PI * 2 / 6) * i - Math.PI / 2;
          const x = centerX + Math.cos(angle) * r3;
          const y = centerY + Math.sin(angle) * r3;
          
          // 司天用蓝色标记，在泉用橙色标记
          const isSitian = i < 3;
          ctx.fillStyle = isSitian ? '#e6f7ff' : '#fff7e6';
          ctx.beginPath();
          ctx.arc(x, y, 10, 0, Math.PI * 2);
          ctx.fill();
          
          // 圆边框
          ctx.strokeStyle = isSitian ? '#1890ff' : '#faad14';
          ctx.lineWidth = 1.5;
          ctx.beginPath();
          ctx.arc(x, y, 10, 0, Math.PI * 2);
          ctx.stroke();
          
          // 外圆显示节气
          ctx.fillStyle = '#333333';
          ctx.font = 'bold 8px Arial';
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText(seasonData[i], x, y);
        }
      });
  },
  onBack() {
    wx.navigateBack();
  }
});
