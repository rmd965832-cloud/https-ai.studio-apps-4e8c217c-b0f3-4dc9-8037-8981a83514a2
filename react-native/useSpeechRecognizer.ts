import { useState, useEffect, useCallback, useRef } from 'react';
import {
  NativeModules,
  NativeEventEmitter,
  Platform,
  PermissionsAndroid,
} from 'react-native';

const { SpeechRecognizerModule } = NativeModules;

export type SpeechStatus =
  | 'idle'
  | 'requesting_permission'
  | 'ready'
  | 'listening'
  | 'recognizing'
  | 'error';

export interface SpeechRecognizerState {
  transcript: string;
  interimTranscript: string;
  isListening: boolean;
  status: SpeechStatus;
  rmsDb: number;
  error: string | null;
  startListening: (locale?: string) => Promise<boolean>;
  stopListening: () => Promise<void>;
  cancel: () => Promise<void>;
  clearTranscript: () => void;
}

/**
 * Custom React Native Hook for Android SpeechRecognizer.
 * Provides real-time transcription streams, interim results, RMS audio level, and status updates.
 */
export function useSpeechRecognizer(): SpeechRecognizerState {
  const [transcript, setTranscript] = useState<string>('');
  const [interimTranscript, setInterimTranscript] = useState<string>('');
  const [isListening, setIsListening] = useState<boolean>(false);
  const [status, setStatus] = useState<SpeechStatus>('idle');
  const [rmsDb, setRmsDb] = useState<number>(0);
  const [error, setError] = useState<string | null>(null);

  const eventEmitterRef = useRef<NativeEventEmitter | null>(null);

  useEffect(() => {
    if (Platform.OS !== 'android' || !SpeechRecognizerModule) {
      return;
    }

    const eventEmitter = new NativeEventEmitter(SpeechRecognizerModule);
    eventEmitterRef.current = eventEmitter;

    // 1. Ready for speech listener
    const onReadySub = eventEmitter.addListener('onSpeechReady', () => {
      setStatus('ready');
      setError(null);
    });

    // 2. User started speaking
    const onStartSub = eventEmitter.addListener('onSpeechStart', () => {
      setIsListening(true);
      setStatus('listening');
    });

    // 3. Audio volume level update (for acoustic wave or orb visualizers)
    const onRmsSub = eventEmitter.addListener('onSpeechRmsChanged', (event: { rmsDb: number }) => {
      setRmsDb(event.rmsDb);
    });

    // 4. Live interim partial results
    const onPartialSub = eventEmitter.addListener(
      'onSpeechPartialResults',
      (event: { partialText: string }) => {
        setInterimTranscript(event.partialText);
        setStatus('recognizing');
      }
    );

    // 5. Final speech recognition results
    const onResultsSub = eventEmitter.addListener(
      'onSpeechResults',
      (event: { transcript: string; matches?: string[] }) => {
        setTranscript(event.transcript);
        setInterimTranscript('');
        setIsListening(false);
        setStatus('idle');
      }
    );

    // 6. User stopped speaking
    const onEndSub = eventEmitter.addListener('onSpeechEnd', () => {
      setIsListening(false);
      setStatus('recognizing');
    });

    // 7. Error event
    const onErrorSub = eventEmitter.addListener(
      'onSpeechError',
      (event: { code: number; message: string }) => {
        setError(event.message);
        setIsListening(false);
        setStatus('error');
      }
    );

    return () => {
      onReadySub.remove();
      onStartSub.remove();
      onRmsSub.remove();
      onPartialSub.remove();
      onResultsSub.remove();
      onEndSub.remove();
      onErrorSub.remove();
      if (SpeechRecognizerModule.destroy) {
        SpeechRecognizerModule.destroy();
      }
    };
  }, []);

  const requestAudioPermission = async (): Promise<boolean> => {
    if (Platform.OS !== 'android') return true;
    try {
      setStatus('requesting_permission');
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
        {
          title: 'Microphone Permission',
          message: 'Speech recognition requires access to your microphone.',
          buttonPositive: 'Allow',
          buttonNegative: 'Deny',
        }
      );
      return granted === PermissionsAndroid.RESULTS.GRANTED;
    } catch (err) {
      setError('Permission request failed');
      return false;
    }
  };

  const startListening = useCallback(
    async (locale: string = 'en-US'): Promise<boolean> => {
      if (Platform.OS !== 'android') {
        setError('Android SpeechRecognizer is only supported on Android');
        return false;
      }

      if (!SpeechRecognizerModule) {
        setError('SpeechRecognizerModule is not linked in NativeModules');
        return false;
      }

      setError(null);
      setInterimTranscript('');

      const hasPermission = await requestAudioPermission();
      if (!hasPermission) {
        setError('Microphone permission denied');
        setStatus('error');
        return false;
      }

      try {
        await SpeechRecognizerModule.startListening(locale);
        setIsListening(true);
        setStatus('ready');
        return true;
      } catch (e: any) {
        setError(e?.message || 'Failed to start SpeechRecognizer');
        setStatus('error');
        return false;
      }
    },
    []
  );

  const stopListening = useCallback(async () => {
    if (SpeechRecognizerModule && isListening) {
      try {
        await SpeechRecognizerModule.stopListening();
      } catch (e: any) {
        setError(e?.message || 'Failed to stop listening');
      }
    }
  }, [isListening]);

  const cancel = useCallback(async () => {
    if (SpeechRecognizerModule) {
      try {
        await SpeechRecognizerModule.cancel();
        setIsListening(false);
        setStatus('idle');
      } catch (e: any) {
        setError(e?.message || 'Failed to cancel');
      }
    }
  }, []);

  const clearTranscript = useCallback(() => {
    setTranscript('');
    setInterimTranscript('');
    setError(null);
  }, []);

  return {
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
  };
}
