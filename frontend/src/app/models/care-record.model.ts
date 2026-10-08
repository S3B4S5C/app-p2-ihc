export interface CareRecord {
  id: string;
  userId: string;
  petName: string;
  care: string;
  animalType: string;
  careDate: string;
  createdAt: string;
}

export interface CreateCareRecordRequest {
  petName: string;
  care: string;
  animalType: string;
  careDate: string;
}