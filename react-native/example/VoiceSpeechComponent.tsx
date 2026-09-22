import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
} from 'react-native';
import { useSpeechRecognizer } from '../useSpeechRecognizer';

export const VoiceSpeechComponent = () => {
  const {
    transcript,
    interimTranscript,
    isListening,
    status,
    rmsDb,
    error,
    startListening,
    stopListening,
    cancel,
    clearTranscript,
  } = useSpeechRecognizer();

  const handleToggleListening = () => {
    if (isListening) {
      stopListening();
    } else {
      startListening('en-US'); // Or 'as-IN', 'hi-IN', 'bn-IN'
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Android Speech Recognizer</Text>

      {/* Status Pill */}
      <View style={[styles.statusBadge, isListening && styles.statusBadgeActive]}>
        <Text style={styles.statusText}>STATUS: {status.toUpperCase()}</Text>
      </View>

      {/* Audio Visualizer Level Bar */}
      {isListening && (
        <View style={styles.visualizerContainer}>
          <Text style={styles.rmsText}>Volume: {rmsDb.toFixed(1)} dB</Text>
          <View
            style={[
              styles.rmsBar,
              { width: `${Math.min(100, Math.max(10, (rmsDb + 2) * 8))}%` },
            ]}
          />
        </View>
      )}

      {/* Real-time Streaming Transcription Display */}
      <View style={styles.transcriptBox}>
        <Text style={styles.label}>Transcribed Text:</Text>
        <Text style={styles.transcriptText}>
          {transcript || (
            <Text style={styles.placeholder}>
              {isListening ? 'Listening...' : 'Press "Start Listening" to speak'}
            </Text>
          )}
        </Text>

        {/* Live Partial / Interim Stream */}
        {interimTranscript.length > 0 && (
          <View style={styles.interimContainer}>
            <ActivityIndicator size="small" color="#00FFC8" />
            <Text style={styles.interimText}> {interimTranscript}...</Text>
          </View>
        )}
      </View>

      {/* Error alert */}
      {error && <Text style={styles.errorText}>⚠️ {error}</Text>}

      {/* Controls */}
      <View style={styles.buttonRow}>
        <TouchableOpacity
          style={[styles.button, isListening ? styles.stopButton : styles.startButton]}
          onPress={handleToggleListening}
        >
          <Text style={styles.buttonText}>
            {isListening ? '🛑 Stop Listening' : '🎙️ Start Listening'}
          </Text>
        </TouchableOpacity>

        {isListening && (
          <TouchableOpacity style={[styles.button, styles.cancelButton]} onPress={cancel}>
            <Text style={styles.buttonText}>Cancel</Text>
          </TouchableOpacity>
        )}

        {transcript.length > 0 && (
          <TouchableOpacity
            style={[styles.button, styles.clearButton]}
            onPress={clearTranscript}
          >
            <Text style={styles.buttonText}>Clear</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#05070A',
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#1A2B26',
    margin: 16,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#00FFC8',
    marginBottom: 12,
    textAlign: 'center',
  },
  statusBadge: {
    alignSelf: 'center',
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
    backgroundColor: 'rgba(255, 255, 255, 0.08)',
    marginBottom: 16,
  },
  statusBadgeActive: {
    backgroundColor: 'rgba(0, 255, 200, 0.2)',
    borderColor: '#00FFC8',
    borderWidth: 1,
  },
  statusText: {
    fontSize: 11,
    fontWeight: '600',
    color: '#BAFFEF',
    letterSpacing: 1,
  },
  visualizerContainer: {
    marginBottom: 16,
    alignItems: 'center',
  },
  rmsText: {
    color: '#6FA89F',
    fontSize: 12,
    marginBottom: 4,
  },
  rmsBar: {
    height: 6,
    backgroundColor: '#00FFC8',
    borderRadius: 3,
  },
  transcriptBox: {
    backgroundColor: 'rgba(255, 255, 255, 0.04)',
    borderColor: '#334155',
    borderWidth: 1,
    borderRadius: 12,
    padding: 14,
    minHeight: 100,
    marginBottom: 16,
  },
  label: {
    fontSize: 11,
    fontWeight: '700',
    color: '#6FA89F',
    marginBottom: 6,
    letterSpacing: 1,
  },
  transcriptText: {
    fontSize: 16,
    color: '#FFFFFF',
    lineHeight: 24,
  },
  placeholder: {
    color: '#64748B',
    fontStyle: 'italic',
  },
  interimContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 8,
  },
  interimText: {
    color: '#00FFC8',
    fontStyle: 'italic',
    fontSize: 14,
  },
  errorText: {
    color: '#EF4444',
    fontSize: 13,
    marginBottom: 12,
    textAlign: 'center',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 10,
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 18,
    borderRadius: 10,
    alignItems: 'center',
  },
  startButton: {
    backgroundColor: '#00FFC8',
  },
  stopButton: {
    backgroundColor: '#EF4444',
  },
  cancelButton: {
    backgroundColor: '#334155',
  },
  clearButton: {
    backgroundColor: 'rgba(255, 255, 255, 0.1)',
  },
  buttonText: {
    color: '#05070A',
    fontWeight: 'bold',
    fontSize: 14,
  },
});
