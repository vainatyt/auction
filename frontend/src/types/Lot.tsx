export interface Lot {
  id: number;
  name: string;
  description: string;
  currentCost: number;
  rateStep: number;
  startAuction: string;
  endAuction: string;
  buyerId?: number;
  ownerId?: number;
  uuid?: string;
}   