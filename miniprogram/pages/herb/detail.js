import * as herbApi from '../../api/herb.js';

Page({
  data: {
    herb: {},
    tasteTags: [],
    effectsList: [],
    natureColor: '#457B9D'
  },
  onLoad(options) {
    // 检查登录状态，未登录则跳转到登录页
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
    
    const id = options.id;
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' });
      this.onBack();
      return;
    }
    this.loadDetail(id);
  },
  onShow() {
    // 每次页面显示时都检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      // 如果未登录或用户取消登录，停止页面加载
      return;
    }
  },
  loadDetail(id) {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    herbApi.getHerbDetail(id).then(data => {
      const herb = this.formatHerbData(data);
      const tasteTags = herb.taste || [];
      const effectsList = herb.effects || [];
      const natureColor = this.getNatureColor(herb.natureClass);
      this.setData({ herb, tasteTags, effectsList, natureColor });
    }).catch(err => {
      wx.showToast({ title: '加载详情失败', icon: 'none' });
      console.error('加载详情失败:', err);
      this.onBack();
    });
  },
  formatHerbData(herb) {
    // 修正数据提取逻辑，从data属性中获取实际数据
    const actualHerb = herb?.data || herb;
    
    return {
      id: actualHerb.id,
      number: actualHerb.number,
      name: actualHerb.name,
      alias: actualHerb.alias || '',
      category: actualHerb.category,
      categoryIcon: actualHerb.categoryIcon,
      properties: actualHerb.properties || '',
      taste: actualHerb.taste ? actualHerb.taste.split(',') : [],
      natureClass: actualHerb.natureClass,
      effects: actualHerb.effects ? actualHerb.effects.split(',') : [],
      description: actualHerb.description || '',
      is_collected: false
    };
  },
  getNatureColor(natureClass) {
    const colors = {
      'hot': '#E63946',
      'warm': '#F4D06F',
      'neutral': '#CCCCCC',
      'cool': '#457B9D',
      'cold': '#457B9D'
    };
    return colors[natureClass] || '#457B9D';
  },
  onBack() {
    wx.navigateBack({ delta: 1 });
  },
  onToggleCollect() {
    // 检查登录状态
    const { requireLogin } = require('../../utils/auth.js');
    if (!requireLogin()) {
      return;
    }
    
    const herb = this.data.herb;
    herb.is_collected = !herb.is_collected;
    this.setData({ herb });
    wx.vibrateShort();
    wx.showToast({ title: herb.is_collected ? '已收藏' : '已取消收藏', icon: 'none' });
  }
});