import React, { useState } from 'react';
import { 
  Bot, MessageSquare, Mic, Image as ImageIcon, CheckSquare, Settings, 
  Send, Sparkles, Volume2, Shield, User, RefreshCw, Key, HardDrive, 
  Cpu, Layers, CheckCircle2, ChevronRight, Play, Download
} from 'lucide-react';

interface Message {
  id: string;
  sender: 'user' | 'ai';
  text: string;
  timestamp: string;
}

interface Task {
  id: string;
  title: string;
  completed: boolean;
  category: string;
}

export default function App() {
  const [activeTab, setActiveTab] = useState<'chat' | 'voice' | 'image' | 'tasks' | 'settings'>('chat');
  
  // Chat State
  const [messages, setMessages] = useState<Message[]>([
    { id: '1', sender: 'ai', text: 'Hello! I am USMAN AI. How can I help you today?', timestamp: '10:00 AM' }
  ]);
  const [inputText, setInputText] = useState('');
  const [isTyping, setIsTyping] = useState(false);

  // Voice State
  const [ttsText, setTtsText] = useState('Welcome to USMAN AI Voice Studio. Experience next-gen speech synthesis.');
  const [isPlayingVoice, setIsPlayingVoice] = useState(false);

  // Image State
  const [prompt, setPrompt] = useState('Futuristic cyberpunk city at sunset with glowing neon lights');
  const [isGeneratingImg, setIsGeneratingImg] = useState(false);
  const [generatedImage, setGeneratedImage] = useState<string | null>('https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1000&auto=format&fit=crop');

  // Tasks State (Room DB simulation)
  const [tasks, setTasks] = useState<Task[]>([
    { id: '1', title: 'Review Gemini API configuration', completed: true, category: 'Development' },
    { id: '2', title: 'Deploy USMAN AI to Netlify', completed: false, category: 'DevOps' },
    { id: '3', title: 'Optimize Jetpack Compose layouts', completed: true, category: 'Mobile' },
  ]);
  const [newTaskTitle, setNewTaskTitle] = useState('');

  // Settings State
  const [apiKey, setApiKey] = useState('AIzaSyD-USMAN-AI-SECURE-KEY-SAMPLE');
  const [modelName, setModelName] = useState('gemini-2.5-flash');

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const userMsg: Message = {
      id: Date.now().toString(),
      sender: 'user',
      text: inputText,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages(prev => [...prev, userMsg]);
    setInputText('');
    setIsTyping(true);

    setTimeout(() => {
      const aiReply: Message = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: `USMAN AI received your query: "${userMsg.text}". All systems operating normally with full multimodal capability.`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };
      setMessages(prev => [...prev, aiReply]);
      setIsTyping(false);
    }, 1000);
  };

  const handleAddTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTaskTitle.trim()) return;
    setTasks(prev => [
      ...prev,
      { id: Date.now().toString(), title: newTaskTitle, completed: false, category: 'General' }
    ]);
    setNewTaskTitle('');
  };

  const toggleTask = (id: string) => {
    setTasks(prev => prev.map(t => t.id === id ? { ...t, completed: !t.completed } : t));
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col md:flex-row">
      {/* Sidebar Navigation */}
      <aside className="w-full md:w-64 bg-slate-900 border-r border-slate-800 flex flex-col justify-between p-4">
        <div>
          <div className="flex items-center gap-3 px-2 py-3 mb-6">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center shadow-lg shadow-indigo-500/30">
              <Sparkles className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="font-bold text-lg tracking-tight bg-gradient-to-r from-white via-indigo-200 to-indigo-400 bg-clip-text text-transparent">USMAN AI</h1>
              <p className="text-xs text-slate-400">Netlify Web Edition</p>
            </div>
          </div>

          <nav className="space-y-1.5">
            <button
              onClick={() => setActiveTab('chat')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                activeTab === 'chat' 
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30' 
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <MessageSquare className="w-4 h-4" />
              AI Chat Assistant
            </button>

            <button
              onClick={() => setActiveTab('voice')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                activeTab === 'voice' 
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30' 
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <Mic className="w-4 h-4" />
              Voice & TTS Studio
            </button>

            <button
              onClick={() => setActiveTab('image')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                activeTab === 'image' 
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30' 
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <ImageIcon className="w-4 h-4" />
              Image Studio
            </button>

            <button
              onClick={() => setActiveTab('tasks')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                activeTab === 'tasks' 
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30' 
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <CheckSquare className="w-4 h-4" />
              Local DB / Tasks
            </button>

            <button
              onClick={() => setActiveTab('settings')}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                activeTab === 'settings' 
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30' 
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
              }`}
            >
              <Settings className="w-4 h-4" />
              Settings & API
            </button>
          </nav>
        </div>

        <div className="pt-4 border-t border-slate-800/80">
          <div className="bg-slate-950/60 rounded-xl p-3 border border-slate-800/60 text-xs">
            <div className="flex items-center justify-between mb-1.5">
              <span className="text-slate-400">System Status</span>
              <span className="flex items-center gap-1.5 text-emerald-400 font-medium">
                <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                Online
              </span>
            </div>
            <p className="text-slate-500">Netlify SPA Rewrite Active</p>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col h-screen overflow-hidden bg-slate-950">
        {/* Top Header */}
        <header className="h-16 border-b border-slate-800 bg-slate-900/50 backdrop-blur px-6 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h2 className="font-semibold text-slate-100 capitalize">
              {activeTab === 'chat' && 'AI Assistant Chat'}
              {activeTab === 'voice' && 'Text-to-Speech & Voice Synthesis'}
              {activeTab === 'image' && 'AI Image Generation Studio'}
              {activeTab === 'tasks' && 'Room Database & Task Management'}
              {activeTab === 'settings' && 'Configuration & API Settings'}
            </h2>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-xs px-2.5 py-1 rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
              v1.0.0 Production
            </span>
          </div>
        </header>

        {/* Tab Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {/* CHAT TAB */}
          {activeTab === 'chat' && (
            <div className="max-w-4xl mx-auto h-full flex flex-col">
              <div className="flex-1 space-y-4 overflow-y-auto pr-2 mb-4">
                {messages.map(msg => (
                  <div key={msg.id} className={`flex gap-3 ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
                    {msg.sender === 'ai' && (
                      <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center shrink-0">
                        <Bot className="w-5 h-5 text-white" />
                      </div>
                    )}
                    <div className={`max-w-[80%] rounded-2xl px-4 py-3 text-sm leading-relaxed ${
                      msg.sender === 'user' 
                        ? 'bg-indigo-600 text-white rounded-br-none shadow-md shadow-indigo-600/20' 
                        : 'bg-slate-900 text-slate-200 border border-slate-800 rounded-bl-none'
                    }`}>
                      <p>{msg.text}</p>
                      <span className={`block text-[10px] mt-1 ${msg.sender === 'user' ? 'text-indigo-200 text-right' : 'text-slate-500'}`}>
                        {msg.timestamp}
                      </span>
                    </div>
                    {msg.sender === 'user' && (
                      <div className="w-8 h-8 rounded-lg bg-slate-800 flex items-center justify-center shrink-0">
                        <User className="w-5 h-5 text-indigo-400" />
                      </div>
                    )}
                  </div>
                ))}
                {isTyping && (
                  <div className="flex gap-3 items-center text-slate-500 text-sm">
                    <Bot className="w-5 h-5 text-indigo-500 animate-spin" />
                    <span>USMAN AI is thinking...</span>
                  </div>
                )}
              </div>

              <form onSubmit={handleSendMessage} className="flex gap-2 bg-slate-900 border border-slate-800 p-2 rounded-2xl">
                <input
                  type="text"
                  value={inputText}
                  onChange={e => setInputText(e.target.value)}
                  placeholder="Ask USMAN AI anything..."
                  className="flex-1 bg-transparent border-none outline-none px-4 text-slate-100 placeholder:text-slate-500 text-sm"
                />
                <button
                  type="submit"
                  className="bg-indigo-600 hover:bg-indigo-500 text-white px-5 py-2.5 rounded-xl font-medium text-sm transition-all shadow-md shadow-indigo-600/30 flex items-center gap-2"
                >
                  <Send className="w-4 h-4" />
                  Send
                </button>
              </form>
            </div>
          )}

          {/* VOICE TAB */}
          {activeTab === 'voice' && (
            <div className="max-w-2xl mx-auto space-y-6">
              <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-xl">
                    <Volume2 className="w-6 h-6" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-slate-100">Text-to-Speech Engine</h3>
                    <p className="text-xs text-slate-400">Synthesize natural speech instantly</p>
                  </div>
                </div>

                <textarea
                  value={ttsText}
                  onChange={e => setTtsText(e.target.value)}
                  rows={4}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-4 text-slate-100 text-sm focus:outline-none focus:border-indigo-500 transition-all mb-4 resize-none"
                />

                <button
                  onClick={() => {
                    setIsPlayingVoice(true);
                    setTimeout(() => setIsPlayingVoice(false), 3000);
                  }}
                  disabled={isPlayingVoice}
                  className="w-full bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-medium py-3 rounded-xl transition-all shadow-md shadow-indigo-600/30 flex items-center justify-center gap-2 text-sm"
                >
                  <Play className={`w-4 h-4 ${isPlayingVoice ? 'animate-bounce' : ''}`} />
                  {isPlayingVoice ? 'Synthesizing & Playing Audio...' : 'Play Speech Synthesis'}
                </button>
              </div>
            </div>
          )}

          {/* IMAGE TAB */}
          {activeTab === 'image' && (
            <div className="max-w-3xl mx-auto space-y-6">
              <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl">
                <div className="flex items-center gap-3 mb-4">
                  <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-xl">
                    <ImageIcon className="w-6 h-6" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-slate-100">AI Image Generator</h3>
                    <p className="text-xs text-slate-400">Create stunning visuals with AI prompts</p>
                  </div>
                </div>

                <div className="flex gap-2 mb-6">
                  <input
                    type="text"
                    value={prompt}
                    onChange={e => setPrompt(e.target.value)}
                    placeholder="Enter image prompt..."
                    className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                  />
                  <button
                    onClick={() => {
                      setIsGeneratingImg(true);
                      setTimeout(() => setIsGeneratingImg(false), 1500);
                    }}
                    disabled={isGeneratingImg}
                    className="bg-indigo-600 hover:bg-indigo-500 text-white px-6 py-3 rounded-xl font-medium text-sm transition-all shadow-md shadow-indigo-600/30 flex items-center gap-2 shrink-0"
                  >
                    {isGeneratingImg ? <RefreshCw className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
                    Generate
                  </button>
                </div>

                {generatedImage && (
                  <div className="relative rounded-xl overflow-hidden border border-slate-800 group">
                    <img src={generatedImage} alt="Generated AI Art" className="w-full h-80 object-cover" />
                    <div className="absolute inset-0 bg-slate-950/60 opacity-0 group-hover:opacity-100 transition-all flex items-center justify-center gap-3">
                      <a
                        href={generatedImage}
                        target="_blank"
                        rel="noreferrer"
                        className="bg-indigo-600 text-white px-4 py-2 rounded-xl text-sm font-medium flex items-center gap-2 shadow-lg"
                      >
                        <Download className="w-4 h-4" /> Download Image
                      </a>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* TASKS TAB */}
          {activeTab === 'tasks' && (
            <div className="max-w-3xl mx-auto space-y-6">
              <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl">
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-xl">
                    <HardDrive className="w-6 h-6" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-slate-100">Local Database & Task Sync</h3>
                    <p className="text-xs text-slate-400">Offline-first Room DB synchronization</p>
                  </div>
                </div>

                <form onSubmit={handleAddTask} className="flex gap-2 mb-6">
                  <input
                    type="text"
                    value={newTaskTitle}
                    onChange={e => setNewTaskTitle(e.target.value)}
                    placeholder="Add new task..."
                    className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                  />
                  <button
                    type="submit"
                    className="bg-indigo-600 hover:bg-indigo-500 text-white px-6 py-3 rounded-xl font-medium text-sm transition-all shadow-md shadow-indigo-600/30"
                  >
                    Add Task
                  </button>
                </form>

                <div className="space-y-3">
                  {tasks.map(task => (
                    <div
                      key={task.id}
                      onClick={() => toggleTask(task.id)}
                      className={`flex items-center justify-between p-4 rounded-xl border transition-all cursor-pointer ${
                        task.completed 
                          ? 'bg-slate-950/40 border-slate-800/40 opacity-75' 
                          : 'bg-slate-950 border-slate-800 hover:border-indigo-500/50'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <div className={`w-5 h-5 rounded-lg border flex items-center justify-center transition-all ${
                          task.completed ? 'bg-indigo-600 border-indigo-600 text-white' : 'border-slate-700'
                        }`}>
                          {task.completed && <CheckCircle2 className="w-4 h-4" />}
                        </div>
                        <span className={`text-sm font-medium ${task.completed ? 'line-through text-slate-500' : 'text-slate-200'}`}>
                          {task.title}
                        </span>
                      </div>
                      <span className="text-xs px-2.5 py-1 rounded-full bg-slate-900 text-slate-400 border border-slate-800">
                        {task.category}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* SETTINGS TAB */}
          {activeTab === 'settings' && (
            <div className="max-w-2xl mx-auto space-y-6">
              <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-6">
                <div className="flex items-center gap-3">
                  <div className="p-3 bg-indigo-500/10 text-indigo-400 rounded-xl">
                    <Key className="w-6 h-6" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-slate-100">API & Authentication Settings</h3>
                    <p className="text-xs text-slate-400">Configure Gemini API key and credentials</p>
                  </div>
                </div>

                <div className="space-y-4">
                  <div>
                    <label className="block text-xs font-medium text-slate-400 mb-2">Gemini API Key</label>
                    <input
                      type="password"
                      value={apiKey}
                      onChange={e => setApiKey(e.target.value)}
                      className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-indigo-500 font-mono"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-medium text-slate-400 mb-2">Active AI Model</label>
                    <select
                      value={modelName}
                      onChange={e => setModelName(e.target.value)}
                      className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-indigo-500"
                    >
                      <option value="gemini-2.5-flash">Gemini 2.5 Flash (Recommended)</option>
                      <option value="gemini-pro">Gemini 1.5 Pro</option>
                    </select>
                  </div>

                  <button
                    onClick={() => alert('Settings saved successfully!')}
                    className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-medium py-3 rounded-xl transition-all shadow-md shadow-indigo-600/30 text-sm"
                  >
                    Save Changes
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
