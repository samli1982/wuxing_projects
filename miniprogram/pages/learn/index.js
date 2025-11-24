Page({
  data: {
    activeCategory: '基础理论',
    activeTab: 'qa',
    categories: ['基础理论', '五运六气', '八字命理', '药精解读', '伤寒推病', '天文历法'],
    
    recommendedCourses: [
      {
        course_id: 'course_001',
        title: '《五气经天图详解》',
        description: '从天文到五行的宇宙能量流转解析',
        progress: 30,
        icon: '📖'
      },
      {
        course_id: 'course_002',
        title: '《阴阳五行基本法则》',
        description: '中医理论的根基与应用',
        progress: 50,
        icon: '☯️'
      }
    ],

    coursesList: [
      {
        course_id: 'course_001',
        category: '基础理论',
        title: '阴阳五行基本法则',
        course_icon: '📚',
        duration: '15分钟',
        type: '文图',
        progress: 60,
        is_collected: true
      },
      {
        course_id: 'course_002',
        category: '基础理论',
        title: '天干地支的宇宙密码',
        course_icon: '📚',
        duration: '20分钟',
        type: '视频',
        progress: 40,
        is_collected: false
      },
      {
        course_id: 'course_003',
        category: '基础理论',
        title: '五脏与五行的对应关系',
        course_icon: '🫀',
        duration: '18分钟',
        type: '文图',
        progress: 0,
        is_collected: false
      }
    ],

    classicsList: [
      {
        classic_id: 'suwen_001',
        name: '素问·上古天真论',
        excerpt: '法于阴阳，和于术数，饮食有节，起居有常，不妄作劳，故能形与神俱，而尽终其天年，度百岁乃去。',
        annotation: '此为中医养生的最高准则，强调顺应自然规律、调理饮食起居的重要性',
        related_course: 'course_001'
      },
      {
        classic_id: 'lingshu_001',
        name: '灵枢·本藏',
        excerpt: '心藏神，肺藏魄，肝藏魂，脾藏意，肾藏志，此五脏所藏也。',
        annotation: '五脏与精神活动的对应关系，是中医整体观念的重要体现',
        related_course: 'course_003'
      }
    ],

    videosList: [
      {
        video_id: 'video_001',
        title: '五气经天图详解',
        speaker: '一真老师',
        duration: '28:00',
        views: 12000,
        views_text: '1.2万人观看',
        thumbnail_url: 'https://via.placeholder.com/150'
      },
      {
        video_id: 'video_002',
        title: '如何找到适合自己的体质调理方案',
        speaker: '云老师',
        duration: '35:00',
        views: 8500,
        views_text: '8500人观看',
        thumbnail_url: 'https://via.placeholder.com/150'
      }
    ],

    qaList: [
      {
        qa_id: 'qa_001',
        question: '丙辛合化水是什么意思？',
        answer: '这是天干五合之一。丙与辛相合，化为水。代表阴阳相济、相互制约与转化的关系。',
        likes: 234
      },
      {
        qa_id: 'qa_002',
        question: '如何根据五行判断体质？',
        answer: '可以通过观察舌象、体型、脾气性格等多个方面综合判断。一个人可能同时具有多种体质特征。',
        likes: 456
      }
    ],

    notesList: [
      {
        note_id: 'note_001',
        title: '喜用神判断逻辑',
        preview: '日主弱，需要扶抑。喜用神是用来补救命局的关键因素...',
        created_at: '2025-11-20'
      },
      {
        note_id: 'note_002',
        title: '肉桂炮炙要点',
        preview: '肉桂分生用与炮炙。生用温阳散寒，炮炙后活血通经...',
        created_at: '2025-11-18'
      }
    ]
  },

  onLoad() {
    console.log('✅ 学堂首页已加载');
    this.loadContent();
  },

  loadContent() {
    // 根据activeCategory筛选课程列表
    this.filterCourses();
  },

  filterCourses() {
    // 实际应用中可根据分类动态加载内容
    const filtered = this.data.coursesList.filter(c => c.category === this.data.activeCategory);
    this.setData({ coursesList: filtered.length > 0 ? filtered : this.data.coursesList });
  },

  switchCategory(e) {
    const category = e.currentTarget.dataset.category;
    this.setData({ activeCategory: category });
    this.filterCourses();
  },

  switchCommunityTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
  },

  onBack() {
    wx.navigateBack();
  },

  onSearch() {
    wx.showToast({ title: '搜索功能待实现', icon: 'none' });
  },

  onCourseDetail(e) {
    const courseId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/learn/course?id=${courseId}` });
  },

  toggleCollect(e) {
    const courseId = e.currentTarget.dataset.id;
    const coursesList = this.data.coursesList.map(c => {
      if (c.course_id === courseId) {
        c.is_collected = !c.is_collected;
      }
      return c;
    });
    this.setData({ coursesList });
    wx.showToast({ title: '已' + (coursesList.find(c => c.course_id === courseId).is_collected ? '收藏' : '取消收藏'), icon: 'none' });
  },

  onClassicDetail(e) {
    const classicId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/learn/classic?id=${classicId}` });
  },

  onAnnotation(e) {
    const classicId = e.currentTarget.dataset.id;
    wx.showModal({
      title: '古籍注解',
      content: '此为中医养生的最高准则，强调顺应自然规律、调理饮食起居的重要性',
      showCancel: false
    });
  },

  onRelatedCourse(e) {
    const courseId = e.currentTarget.dataset.courseId;
    wx.navigateTo({ url: `/pages/learn/course?id=${courseId}` });
  },

  onVideoPlay(e) {
    const videoId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/learn/video?id=${videoId}` });
  },

  toggleQaLike(e) {
    const qaId = e.currentTarget.dataset.id;
    wx.showToast({ title: '点赞成功', icon: 'none' });
  },

  toggleQaCollect(e) {
    const qaId = e.currentTarget.dataset.id;
    wx.showToast({ title: '已收藏', icon: 'none' });
  },

  onNoteDetail(e) {
    const noteId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/learn/note?id=${noteId}` });
  },

  onShareAppMessage() {
    return {
      title: '国医学堂 - 中医知识学习中心',
      path: '/pages/learn/index'
    };
  }
});
