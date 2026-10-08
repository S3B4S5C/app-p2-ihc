export type CareRecordStatus = 'PENDING' | 'COMPLETED';

export interface CareRecord {
  id: string;
  userId: string;
  petName: string;
  care: string;
  animalType: string;
  careDate: string;
  createdAt: string;
  status: CareRecordStatus;
}

export interface CreateCareRecordRequest {
  petName: string;
  care: string;
  animalType: string;
  careDate: string;
}